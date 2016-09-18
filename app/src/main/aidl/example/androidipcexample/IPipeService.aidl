package example.androidipcexample;

interface IPipeService {
    ParcelFileDescriptor start();
    void stop();
}
