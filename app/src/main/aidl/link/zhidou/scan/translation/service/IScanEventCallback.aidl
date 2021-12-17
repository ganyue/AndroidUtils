// IScanEventCallback.aidl
package link.zhidou.scan.translation.service;

// Declare any non-default types here with import statements

interface IScanEventCallback {
    void onScanStart ();
    void onScanning (String intermediateResult);
    void onScanStop (String finalResult);
}