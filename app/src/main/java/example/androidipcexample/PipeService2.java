package example.androidipcexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PipeService2 extends Service {
    public PipeService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IPipeService.Stub() {

            private Thread thread;

            @Override
            public ParcelFileDescriptor start() throws RemoteException {
                final ParcelFileDescriptor[] pipeFd;
                try {
                    pipeFd = ParcelFileDescriptor.createPipe();
                } catch (IOException e) {
                    return null;
                }
                Log.d("test", "start");

                thread = new Thread(new Runnable() {
                    // 1MBのダミーデータ
                    private final byte[] jpeg = new byte[500 * 1024];

                    @Override
                    public void run() {
                        try (DataOutputStream os = new DataOutputStream(
                                new BufferedOutputStream(
                                        new ParcelFileDescriptor.AutoCloseOutputStream(pipeFd[1])
                                )
                        )) {
                            while (!Thread.interrupted()) {
                                long beforeTime = System.currentTimeMillis();

                                try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                     ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                                    oos.writeObject(new MetaData(1920, 1080));

                                    byte[] metaDataBytes = bos.toByteArray();
                                    os.writeInt(metaDataBytes.length);
                                    os.write(metaDataBytes);
                                }
                                os.writeInt(jpeg.length);
                                os.write(jpeg);

                                long time = System.currentTimeMillis() - beforeTime;

                                // 60FPSくらいになるようにSleepする。
                                if (time < 16) {
                                    Thread.sleep(16 - time);
                                }
                            }
                        } catch (IOException | InterruptedException e) {
                            // 無視
                        }
                    }
                });
                thread.start();

                return pipeFd[0];
            }

            @Override
            public void stop() throws RemoteException {
                thread.interrupt();
            }
        };
    }
}
