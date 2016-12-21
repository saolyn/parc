package ch.usi.inf.mc.yapt.parc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.usi.inf.mc.yapt.parc.R;

/** Simple dialog fragment to show some text (about and help dialogs) */
public class DialogFragment extends android.app.DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int dialog = getArguments().getInt("dialog", R.layout.about_dialog);

        final View rootView = inflater.inflate(dialog, container, false);

        final Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }
}
