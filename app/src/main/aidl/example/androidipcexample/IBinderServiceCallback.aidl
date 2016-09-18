package example.androidipcexample;

import example.androidipcexample.MetaData;

interface IBinderServiceCallback {
    void render(in MetaData meta, in byte[] jpeg);
}
