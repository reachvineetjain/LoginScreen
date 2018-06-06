package com.nehvin.loginscreen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class SignUpFragment extends AuthFragment{

    @BindViews(value = {
            R.id.email_input_edit,
            R.id.password_input_edit,
            R.id.confirm_password_edit
    })
    protected List<TextInputEditText> views;

    @BindView(R.id.email_input_edit)
    protected TextInputEditText mEmailField;

    @BindView(R.id.password_input_edit)
    protected TextInputEditText mPasswordField;

    @BindView(R.id.confirm_password_edit)
    protected TextInputEditText mConfirmPasswordField;

    private FirebaseAuth mAuth;

    private static final String TAG = SignUpFragment.class.getSimpleName();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view!=null){
            view.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.color_sign_up));
            caption.setText(getString(R.string.sign_up_label));

            for(TextInputEditText editText:views){
                if(editText.getId()==R.id.password_input_edit){
                    final TextInputLayout inputLayout= ButterKnife.findById(view,R.id.password_input);
                    final TextInputLayout confirmLayout=ButterKnife.findById(view,R.id.confirm_password);
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    inputLayout.setTypeface(boldTypeface);
                    confirmLayout.setTypeface(boldTypeface);
                    editText.addTextChangedListener(new TextWatcherAdapter(){
                        @Override
                        public void afterTextChanged(Editable editable) {
                            inputLayout.setPasswordVisibilityToggleEnabled(editable.length()>0);
                        }
                    });
                }
                editText.setOnFocusChangeListener((temp,hasFocus)->{
                    if(!hasFocus){
                        boolean isEnabled=editText.getText().length()>0;
                        editText.setSelected(isEnabled);
                    }
                });
            }
            caption.setVerticalText(true);
            foldStuff();
            caption.setTranslationX(getTextPadding());

        }
    }

    @Override
    public int authLayout() {
        return R.layout.sign_up_fragment;
    }

    @Override
    public void clearFocus() {
        for(View view:views) view.clearFocus();
    }

    @Override
    public void fold() {
        lock=false;
        Rotate transition = new Rotate();
        transition.setEndAngle(-90f);
        transition.addTarget(caption);
        TransitionSet set=new TransitionSet();
        set.setDuration(getResources().getInteger(R.integer.duration));
        ChangeBounds changeBounds=new ChangeBounds();
        set.addTransition(changeBounds);
        set.addTransition(transition);
        TextSizeTransition sizeTransition=new TextSizeTransition();
        sizeTransition.addTarget(caption);
        set.addTransition(sizeTransition);
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addListener(new Transition.TransitionListenerAdapter(){
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(getTextPadding());
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();
//                Log.i(TAG, "onTransitionEnd: signnup frag transition ended");
            }

//            @Override
//            public void onTransitionStart(Transition transition) {
//                super.onTransitionStart(transition);
//                Log.i(TAG, "onTransitionStart: signup transition started");
//            }
        });
        TransitionManager.beginDelayedTransition(parent,set);
        foldStuff();
        caption.setTranslationX(-caption.getWidth()/8+getTextPadding());
    }

    private void foldStuff(){
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,caption.getTextSize()/2f);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params=getParams();
        params.rightToRight=ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias=0.5f;
        caption.setLayoutParams(params);
    }

    private float getTextPadding(){
        return getResources().getDimension(R.dimen.folded_label_padding)/2.1f;
    }

    @Override
    public void authenticate() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String confirmPwd = mConfirmPasswordField.getText().toString();

        if (!validateForm(email,password,confirmPwd)) {
            Log.i(TAG, "authenticate: validation failed");
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Sign Up Failed");
                            alert.setMessage(task.getException().getMessage());
                            alert.setPositiveButton("OK",null);
                            alert.show();
                            Toast.makeText(getActivity(), task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser username) {
        Intent yourAppIntent = new Intent(getContext(), YourAppHere.class);
        if (username != null) {
            yourAppIntent.putExtra("username", username.getEmail());
        } else {
            yourAppIntent.putExtra("username", "Anonymous");
        }
        getActivity().startActivity(yourAppIntent);
    }

    public static boolean isEmailValid(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

//    public static boolean isValidPassword(final String password) {
//
//        Pattern pattern;
//        Matcher matcher;
//        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(password);
//        Log.i(TAG, "isValidPassword: "+matcher.matches());
//        return matcher.matches();
//
//    }

    private boolean validateForm(String email, String password, String confirmPwd) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email cannot be blank");
            valid = false;
        } else {
            if(!isEmailValid(email)){
                mEmailField.setError("Please provide a valid email id");
            }
            else
            {
                mEmailField.setError(null);
            }
        }


        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Password Cannot be blank");
            valid = false;
        } else {
            if(!(password.equals(confirmPwd))){
                mConfirmPasswordField.setError("Passwords do not match");
                valid = false;
            }
            else
            {
                if(password.length() < 8){
                    mPasswordField.setError("Minimum password length is 8");
                    valid = false;
                }
                else {
//                    if(!isValidPassword(password))
//                    {
//                        mPasswordField.setError("Follow the password rules");
//                        valid = false;
//                    }
//                    else{
                        mPasswordField.setError(null);
//                    }
                }

            }
        }
        return valid;
    }
}