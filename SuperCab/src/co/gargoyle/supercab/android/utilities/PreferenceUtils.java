package co.gargoyle.supercab.android.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PreferenceUtils {

  private Context mContext; 
  
  private SharedPreferences mSettings; 

  @Inject
  public PreferenceUtils(Context context) {
    mContext = context;
    mSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
  }

  public void saveLastPhoneState(int state) {
    SharedPreferences.Editor editor = mSettings.edit();
    editor.putInt(PreferenceConstants.KEY_PHONE_STATE, state);
    editor.commit();
  }

  public int getLastPhoneState() {
    return mSettings.getInt(PreferenceConstants.KEY_PHONE_STATE, TelephonyManager.CALL_STATE_IDLE);
  }

  public Context getContext() {
    return mContext;
  }
  
  public Optional<String> getUsername() {
    String username = mSettings.getString(PreferenceConstants.KEY_USERNAME, null);
    if (username == null) {
      return Optional.absent();
    } else {
      return Optional.of(username);
    }
  }
  
  public Optional<String> getPassword() {
    String password = mSettings.getString(PreferenceConstants.KEY_PASSWORD, null);
    if (password == null) {
      return Optional.absent();
    } else {
      return Optional.of(password);
    }
  }

  public boolean hasCredentials() {
    String username = mSettings.getString(PreferenceConstants.KEY_USERNAME, null);
    String password = mSettings.getString(PreferenceConstants.KEY_PASSWORD, null);

    if (StringUtils.stringIsEmpty(username) || StringUtils.stringIsEmpty(password)) {
      return false;
    } else {
      return true;
    }
  }

  public void saveCredentials(String username, String password) {
    SharedPreferences.Editor editor = mSettings.edit();
    editor.putString(PreferenceConstants.KEY_USERNAME, username);
    editor.putString(PreferenceConstants.KEY_PASSWORD, password);
    editor.commit();
  }
}