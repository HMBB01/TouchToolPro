package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import static top.bogey.touch_tool_pro.ui.InstantActivity.ACTION_ID;
import static top.bogey.touch_tool_pro.ui.InstantActivity.TASK_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.number.MathExpressionAction;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool_pro.ui.InstantActivity;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.NodePickerFloatPreview;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetString extends PinWidget<PinString> {
    private final PinWidgetInputBinding binding;

    public PinWidgetString(@NonNull Context context, ActionCard<?> card, PinView pinView, PinString pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetInputBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);

        if (pinObject.getSubType() == PinSubType.NORMAL) {
            binding.editText.setText(String.valueOf(pinObject.getValue()));
            binding.editText.addTextChangedListener(new TextChangedListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    pinObject.setValue(s.toString());
                }
            });
        } else {
            binding.editText.setEnabled(false);
            binding.pickButton.setVisibility(VISIBLE);
        }

        switch (pinObject.getSubType()) {
            case URL -> {
                binding.editText.setText(R.string.action_outer_start_subtitle_copy);
                binding.pickButton.setIconResource(R.drawable.icon_copy);
                binding.pickButton.setOnClickListener(v -> {
                    ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                    String ttp = "ttp://do_action?" + TASK_ID + "=" + card.getFunctionContext().getId() + "&" + ACTION_ID + "=" + card.getAction().getId();

                    ClipData clipData = ClipData.newPlainText(context.getString(R.string.app_name), ttp);
                    manager.setPrimaryClip(clipData);
                    Toast.makeText(context, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
                });
            }
            case SHORTCUT -> {
                binding.editText.setText(R.string.action_outer_start_subtitle_shortcut_create);
                binding.pickButton.setIconResource(R.drawable.icon_export);
                binding.pickButton.setOnClickListener(v -> {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
                        if (shortcutManager.isRequestPinShortcutSupported()) {
                            Intent intent = new Intent(context, InstantActivity.class);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.putExtra(InstantActivity.INTENT_KEY_DO_ACTION, true);
                            intent.putExtra(TASK_ID, card.getFunctionContext().getId());
                            intent.putExtra(ACTION_ID, card.getAction().getId());
                            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, card.getAction().getId()).setShortLabel(card.getFunctionContext().getTitle()).setIcon(Icon.createWithResource(context, R.drawable.icon_shortcut)).setIntent(intent).build();
                            shortcutManager.requestPinShortcut(shortcut, null);
                            return;
                        }
                    }
                    Toast.makeText(context, R.string.device_not_support_shortcut, Toast.LENGTH_SHORT).show();
                });
            }
            case NODE_ID -> {
                binding.editText.setEnabled(true);
                binding.editText.setText(pinObject.getValue());
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinObject.setValue(s.toString());
                    }
                });
                binding.pickButton.setIconResource(R.drawable.icon_widget);
                binding.pickButton.setOnClickListener(v -> new NodePickerFloatPreview(context, new PickerCallback() {
                    @Override
                    public void onComplete() {
                        binding.editText.setText(pinObject.getValue());
                    }
                }, pinObject).show());
            }
            case RINGTONE -> {
                String path = pinObject.getValue();
                binding.editText.setText(getRingtoneName(path));
                binding.pickButton.setIconResource(R.drawable.icon_notification);
                binding.pickButton.setOnClickListener(v -> {
                    MainActivity activity = MainApplication.getInstance().getMainActivity();
                    activity.launcherRingtone(pinObject.getValue(), ((code, intent) -> {
                        if (code == Activity.RESULT_OK) {
                            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            if (uri == null) {
                                pinObject.setValue(null);
                            } else {
                                pinObject.setValue(uri.toString());
                            }
                            binding.editText.setText(getRingtoneName(pinObject.getValue()));
                        }
                    }));
                });
            }
            case MULTI_LINE -> {
                binding.editText.setEnabled(true);
                binding.editText.setText(pinObject.getValue());
                binding.editText.setInputType(binding.editText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                binding.editText.setMaxLines(5);
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinObject.setValue(s.toString());
                    }
                });
                binding.pickButton.setVisibility(GONE);
            }
            case AUTO_PIN -> {
                binding.editText.setEnabled(true);
                binding.editText.setText(pinObject.getValue());
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinObject.setValue(s.toString());
                        refreshMathExpressionDynamicPin(s.toString());
                    }
                });
                binding.pickButton.setVisibility(GONE);
            }
        }
    }

    private String getRingtoneName(String path) {
        Uri uri = Uri.parse(path);
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
        if (ringtone == null) return null;
        return ringtone.getTitle(getContext());
    }

    private void refreshMathExpressionDynamicPin(String path) {
        if (card.getAction() instanceof MathExpressionAction expressionAction) {
            Pattern pattern = Pattern.compile("\\b([a-zA-Z])\\b");
            Matcher matcher = pattern.matcher(path);
            HashSet<String> keys = new HashSet<>();
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }
            ArrayList<Pin> removePins = new ArrayList<>();

            for (Pin pin : expressionAction.calculateMorePins()) {
                String title = pin.getTitle();
                if (keys.isEmpty()) {
                    removePins.add(pin);
                } else if (!keys.remove(title)) {
                    removePins.add(pin);
                }
            }
            removePins.forEach(card::removePin);

            for (String key : keys) {
                Pin paramsPin = new Pin(new PinFloat(), 0, false, true, false);
                paramsPin.setTitle(key);
                card.addPin(paramsPin, 0);
            }
        }
    }

    @Override
    public void initCustom() {
        binding.spinner.setVisibility(VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.add(context.getString(R.string.pin_string));
        adapter.add(context.getString(R.string.pin_string_multi_line));
        adapter.add(context.getString(R.string.pin_string_node_id));
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(subTypeToIndex(pinObject.getSubType()));
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == subTypeToIndex(pinObject.getSubType())) return;
                String value = "";
                Editable text = binding.editText.getText();
                if (text != null) value = text.toString();
                Pin functionPin = ((PinCustomView) pinView).getFunctionPin();
                functionPin.cleanLinks(card.getFunctionContext());
                functionPin.setValue(new PinString(indexToSubType(position), value));
                pinView.refreshPinView();
            }
        });
    }

    private int subTypeToIndex(PinSubType subType) {
        return switch (subType) {
            case MULTI_LINE -> 1;
            case NODE_ID -> 2;
            default -> 0;
        };
    }

    private PinSubType indexToSubType(int index) {
        return switch (index) {
            case 1 -> PinSubType.MULTI_LINE;
            case 2 -> PinSubType.NODE_ID;
            default -> PinSubType.NORMAL;
        };
    }
}
