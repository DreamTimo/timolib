package com.timo.base.tools.permissions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * {@link AppCompatDialogFragment} to display rationale for permission requests when the request
 * comes from a Fragment or Activity that can host a Fragment.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class RationaleDialogFragmentCompat extends AppCompatDialogFragment {

    public static final String TAG = "RationaleDialogFragmentCompat";

    private PermissionUtils.PermissionCallbacks mPermissionCallbacks;
    private PermissionUtils.RationaleCallbacks mRationaleCallbacks;

    public static RationaleDialogFragmentCompat newInstance(
            @NonNull String rationaleMsg,
            @NonNull String positiveButton,
            @NonNull String negativeButton,
            @StyleRes int theme,
            int requestCode,
            @NonNull String[] permissions) {

        // Create new Fragment
        RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();

        // Initialize configuration as arguments
        RationaleDialogConfig config = new RationaleDialogConfig(
                positiveButton, negativeButton, rationaleMsg, theme, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    /**
     * Version of {@link #show(FragmentManager, String)} that no-ops when an IllegalStateException
     * would otherwise occur.
     */
    public void showAllowingStateLoss(FragmentManager manager, String tag) {
//        if (manager.isStateSaved()) {
//            return;
//        }

        show(manager, tag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof PermissionUtils.PermissionCallbacks) {
                mPermissionCallbacks = (PermissionUtils.PermissionCallbacks) getParentFragment();
            }
            if (getParentFragment() instanceof PermissionUtils.RationaleCallbacks){
                mRationaleCallbacks = (PermissionUtils.RationaleCallbacks) getParentFragment();
            }
        }

        if (context instanceof PermissionUtils.PermissionCallbacks) {
            mPermissionCallbacks = (PermissionUtils.PermissionCallbacks) context;
        }

        if (context instanceof PermissionUtils.RationaleCallbacks) {
            mRationaleCallbacks = (PermissionUtils.RationaleCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPermissionCallbacks = null;
        mRationaleCallbacks = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Rationale dialog should not be cancelable
        setCancelable(false);

        // Get config from arguments, create click listener
        RationaleDialogConfig config = new RationaleDialogConfig(getArguments());
        RationaleDialogClickListener clickListener =
                new RationaleDialogClickListener(this, config, mPermissionCallbacks, mRationaleCallbacks);

        // Create an AlertDialog
        return config.createSupportDialog(getContext(), clickListener);
    }
}
