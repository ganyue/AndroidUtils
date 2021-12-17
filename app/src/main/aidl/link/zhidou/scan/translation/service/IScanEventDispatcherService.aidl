// IScanEventDispatcherService.aidl
package link.zhidou.scan.translation.service;
import link.zhidou.scan.translation.service.IScanEventCallback;

// Declare any non-default types here with import statements

interface IScanEventDispatcherService {
    void registerScanCallback(IScanEventCallback callback);
    void unRegisterScanCallback(IScanEventCallback callback);
}