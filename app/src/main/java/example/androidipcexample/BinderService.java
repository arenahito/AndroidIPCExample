package example.androidipcexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class BinderService extends Service {

    public BinderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IBinderService.Stub() {

            private Thread thread;

            @Override
            public void start(final IBinderServiceCallback callback) throws RemoteException {
                thread = new Thread(new Runnable() {
                    // 1MBのダミーデータ
                    private final byte[] jpeg = new byte[1024 * 1024];

                    @Override
                    public void run() {
                        try {
                            while (!Thread.interrupted()) {
                                long beforeTime = System.currentTimeMillis();
                                callback.render(new MetaData(1920, 1080), jpeg);
                                long time = System.currentTimeMillis() - beforeTime;

                                // 60FPSくらいになるようにSleepする。
                                if (time < 16) {
                                    Thread.sleep(16 - time);
                                }
                            }
                        } catch (RemoteException | InterruptedException e) {
                            // 無視
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void stop() throws RemoteException {
                thread.interrupt();
            }
        };
    }

}
