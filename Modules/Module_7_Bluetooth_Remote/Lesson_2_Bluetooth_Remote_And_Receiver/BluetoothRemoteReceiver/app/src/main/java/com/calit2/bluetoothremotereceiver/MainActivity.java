package com.calit2.bluetoothremotereceiver;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/******************************************************************************
 * Name:        MainActivity
 * Description: Activity that determines which Fragment to display as the
 *              screen of the application.
 *****************************************************************************/
public class MainActivity extends FragmentActivity {

  // Log Message
  private static final String TAG = "MainActivity";

  /****************************************************************************
   * Name:        onCreate
   * Description: Determines appropriate Fragment to display based on user's
   *              input
   *
   * @param       savedInstanceState Reference to Bundle object. Activities
   *                                 can be restored to a former state using
   *                                 data saved in this bundle.
   ***************************************************************************/
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      FragmentTransaction transaction = getSupportFragmentManager()
          .beginTransaction();


      /********************************************************************************
      * Make sure you comment out one of the two following fragments
      * depending on what kind of device you are deploying to
      */
      //BluetoothReceiverFragment fragment = BluetoothReceiverFragment.newInstance();
      BluetoothRemoteFragment fragment = BluetoothRemoteFragment.newInstance();
      /********************************************************************************/
      transaction.replace(R.id.content_fragment, fragment);
      transaction.commit();
    }
  }
}
