package example.androidipcexample;

import example.androidipcexample.IBinderServiceCallback;

interface IBinderService {
    void start(in IBinderServiceCallback callback);
    void stop();
}
