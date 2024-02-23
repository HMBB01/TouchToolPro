#ifndef NATIVE_LIB
#define NATIVE_LIB

#include <jni.h>
#include <string>
#include <algorithm>
#include <paddle_api.h>

#include "native-lib.h"
#include "ocr_ppredictor.h"

using namespace std;
using namespace cv;


static jobject createMatchResult(JNIEnv *env, jdouble value, jint x, jint y, jint width, jint height) {
    auto resultClass = (jclass) env->FindClass("top/bogey/touch_tool_pro/utils/MatchResult");
    jmethodID mid = env->GetMethodID(resultClass, "<init>", "(DIIII)V");
    jobject result = env->NewObject(resultClass, mid, value, x, y, width, height);
    return result;
}

static int clamp(int up, int low, int value) {
    return max(low, min(up, value));
}

static paddle::lite_api::PowerMode str_to_cpu_mode(const std::string &cpu_mode) {
    static std::map<std::string, paddle::lite_api::PowerMode> cpu_mode_map{
            {"LITE_POWER_HIGH",      paddle::lite_api::LITE_POWER_HIGH},
            {"LITE_POWER_LOW",       paddle::lite_api::LITE_POWER_HIGH},
            {"LITE_POWER_FULL",      paddle::lite_api::LITE_POWER_FULL},
            {"LITE_POWER_NO_BIND",   paddle::lite_api::LITE_POWER_NO_BIND},
            {"LITE_POWER_RAND_HIGH", paddle::lite_api::LITE_POWER_RAND_HIGH},
            {"LITE_POWER_RAND_LOW",  paddle::lite_api::LITE_POWER_RAND_LOW}};
    std::string upper_key;
    std::transform(cpu_mode.cbegin(), cpu_mode.cend(), upper_key.begin(),
                   ::toupper);
    auto index = cpu_mode_map.find(upper_key);
    if (index == cpu_mode_map.end()) {
        LOGE("cpu_mode not found %s", upper_key.c_str());
        return paddle::lite_api::LITE_POWER_HIGH;
    } else {
        return index->second;
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_top_bogey_touch_1tool_1pro_utils_DisplayUtils_nativeMatchTemplate(JNIEnv *env, jclass clazz, jobject bitmap, jobject temp) {
    int scale = 2;

    Mat src = bitmap_to_cv_mat(env, bitmap);
    Mat tmp = bitmap_to_cv_mat(env, temp);
    if (src.empty() || tmp.empty()) return createMatchResult(env, 0, 0, 0, 0, 0);

    cvtColor(src, src, COLOR_BGR2GRAY);
    cvtColor(tmp, tmp, COLOR_BGR2GRAY);

    resize(src, src, Size(src.cols / scale, src.rows / scale));
    resize(tmp, tmp, Size(tmp.cols / scale, tmp.rows / scale));
    int resultCol = src.cols - tmp.cols + 1;
    int resultRow = src.rows - tmp.rows + 1;

    Mat result;
    result.create(resultCol, resultRow, CV_32FC1);

    matchTemplate(src, tmp, result, TM_CCOEFF_NORMED);

    double minVal = -1;
    double maxVal;
    Point minLoc;
    Point maxLoc;
    minMaxLoc(result, &minVal, &maxVal, &minLoc, &maxLoc);

    jobject matchResult;
    matchResult = createMatchResult(env, maxVal, maxLoc.x * scale, maxLoc.y * scale, tmp.cols * scale, tmp.rows * scale);
    src.release();
    tmp.release();
    result.release();
    return matchResult;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_top_bogey_touch_1tool_1pro_utils_DisplayUtils_nativeMatchAllTemplate(JNIEnv *env, jclass clazz, jobject bitmap, jobject temp, jint match_value) {
    int scale = 1;

    Mat src = bitmap_to_cv_mat(env, bitmap);
    Mat tmp = bitmap_to_cv_mat(env, temp);
    if (src.empty() || tmp.empty()) return createMatchResult(env, 0, 0, 0, 0, 0);

    cvtColor(src, src, COLOR_BGR2GRAY);
    cvtColor(tmp, tmp, COLOR_BGR2GRAY);

    resize(src, src, Size(src.cols / scale, src.rows / scale));
    resize(tmp, tmp, Size(tmp.cols / scale, tmp.rows / scale));
    int resultCol = src.cols - tmp.cols + 1;
    int resultRow = src.rows - tmp.rows + 1;

    Mat result;
    result.create(resultCol, resultRow, CV_32FC1);

    matchTemplate(src, tmp, result, TM_CCOEFF_NORMED);

    jclass listCls = env->FindClass("java/util/ArrayList");
    jmethodID listInit = env->GetMethodID(listCls, "<init>", "()V");
    jobject listObj = env->NewObject(listCls, listInit);
    jmethodID listAdd = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");

    vector<Rect> areas;
    for (int i = 0; i < result.rows; ++i) {
        for (int j = 0; j < result.cols; ++j) {
            bool flag = false;
            for (const auto &area: areas) {
                if (area.contains(Point(j, i))) {
                    flag = true;
                    break;
                }
            }
            if (flag) continue;

            float currValue = result.at<float>(i, j) * 100;
            if (currValue >= match_value) {
                env->CallBooleanMethod(listObj, listAdd, createMatchResult(env, currValue, j * scale, i * scale, tmp.cols * scale, tmp.rows * scale));
                areas.push_back(Rect(j, i, tmp.cols, tmp.rows));
            }
        }
    }

    src.release();
    tmp.release();
    result.release();
    return listObj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_top_bogey_touch_1tool_1pro_utils_DisplayUtils_nativeMatchColor(JNIEnv *env, jclass clazz, jobject bitmap, jintArray hsvColor, jint offset) {
    Mat src = bitmap_to_cv_mat(env, bitmap);
    if (src.empty()) return nullptr;
    cvtColor(src, src, COLOR_BGR2HSV);
    GaussianBlur(src, src, Size(5, 5), 0);
    erode(src, src, 3);

    jint *hsv = env->GetIntArrayElements(hsvColor, JNI_FALSE);
    Scalar color((int) hsv[0], (int) hsv[1], (int) hsv[2]);
    Scalar lowColor(clamp(180, 0, (int) color[0] - offset), clamp(255, 0, (int) color[1] - offset), clamp(255, 0, (int) color[2] - offset));
    Scalar highColor(clamp(180, 0, (int) color[0] + offset), clamp(255, 0, (int) color[1] + offset), clamp(255, 0, (int) color[2] + offset));

    Mat colorImg;
    inRange(src, lowColor, highColor, colorImg);
    vector<vector<Point> > contours;
    findContours(colorImg, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    jclass listCls = env->FindClass("java/util/ArrayList");
    jmethodID listInit = env->GetMethodID(listCls, "<init>", "()V");
    jobject listObj = env->NewObject(listCls, listInit);
    jmethodID listAdd = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");

    for (auto &contour: contours) {
        double area = contourArea(contour);
        if (area > 81) {
            Rect r = boundingRect(contour);
            env->CallBooleanMethod(listObj, listAdd, createMatchResult(env, area, r.x, r.y, r.width, r.height));
        }
    }

    src.release();
    return listObj;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_top_bogey_touch_1tool_1pro_utils_ocr_Predictor_init(JNIEnv *env, jclass clazz, jstring j_det_model_path, jstring j_rec_model_path, jstring j_cls_model_path, jint j_use_opencl, jint j_thread_num, jstring j_cpu_mode) {
    string det_model_path = jstring_to_cpp_string(env, j_det_model_path);
    string rec_model_path = jstring_to_cpp_string(env, j_rec_model_path);
    string cls_model_path = jstring_to_cpp_string(env, j_cls_model_path);
    int thread_num = j_thread_num;
    string cpu_mode = jstring_to_cpp_string(env, j_cpu_mode);
    ppredictor::OCR_Config conf;
    conf.use_opencl = j_use_opencl;
    conf.thread_num = thread_num;
    conf.mode = str_to_cpu_mode(cpu_mode);
    auto *orc_predictor = new ppredictor::OCR_PPredictor{conf};
    orc_predictor->init_from_file(det_model_path, rec_model_path, cls_model_path);
    return reinterpret_cast<jlong>(orc_predictor);
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_top_bogey_touch_1tool_1pro_utils_ocr_Predictor_forward(JNIEnv *env, jobject thiz, jlong j_pointer, jobject j_original_image, jint j_max_size_len, jint j_run_det, jint j_run_cls, jint j_run_rec) {
    LOGI("begin to run native forward");
    if (j_pointer == 0) {
        LOGE("JAVA pointer is NULL");
        return cpp_array_to_jfloatarray(env, nullptr, 0);
    }

    cv::Mat origin = bitmap_to_cv_mat(env, j_original_image);
    if (origin.size == 0) {
        LOGE("origin bitmap cannot convert to CV Mat");
        return cpp_array_to_jfloatarray(env, nullptr, 0);
    }

    int max_size_len = j_max_size_len;
    int run_det = j_run_det;
    int run_cls = j_run_cls;
    int run_rec = j_run_rec;

    auto *predictor = (ppredictor::OCR_PPredictor *) j_pointer;
    std::vector<int64_t> dims_arr;
    std::vector<ppredictor::OCRPredictResult> results = predictor->infer_ocr(origin, max_size_len, run_det, run_cls, run_rec);
    LOGI("infer_ocr finished with boxes %ld", results.size());

    // 这里将std::vector<predictor::OCRPredictResult> 序列化成
    // float数组，传输到java层再反序列化
    std::vector<float> float_arr;
    for (const ppredictor::OCRPredictResult &r: results) {
        float_arr.push_back(r.points.size());
        float_arr.push_back(r.word_index.size());
        float_arr.push_back(r.score);
        // add det point
        for (const std::vector<int> &point: r.points) {
            float_arr.push_back(point.at(0));
            float_arr.push_back(point.at(1));
        }
        // add rec word idx
        for (int index: r.word_index) {
            float_arr.push_back(index);
        }
        // add cls result
        float_arr.push_back(r.cls_label);
        float_arr.push_back(r.cls_score);
    }
    return cpp_array_to_jfloatarray(env, float_arr.data(), float_arr.size());
}

extern "C"
JNIEXPORT void JNICALL
Java_top_bogey_touch_1tool_1pro_utils_ocr_Predictor_release(JNIEnv *env, jobject thiz, jlong j_pointer) {
    if (j_pointer == 0) {
        LOGE("JAVA pointer is NULL");
        return;
    }
    auto *predictor = (ppredictor::OCR_PPredictor *) j_pointer;
    delete predictor;
}

#endif