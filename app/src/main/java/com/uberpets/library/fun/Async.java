package com.uberpets.library.fun;

public interface Async<A> {
    default <B> Async<B> onDone(Function<A, B> f) {
        Async<A> that = this;
        return new Async<B>() {
            @Override
            public void run(Consumer<B> ok, Consumer<Exception> error) {
                that.run(t -> {
                    try {
                        ok.accept(f.apply(t));
                    } catch (Exception e) {
                        error.accept(e);
                    }
                }, error);
            }
        };
    }

    default Async<A> onDone(BiConsumer<A, Exception> f) {
        Async<A> that = this;
        return new Async<A>() {
            @Override
            public void run(Consumer<A> ok, Consumer<Exception> error) {
                that.run(t -> {
                    try {
                        f.accept(t, null);
                        ok.accept(t);
                    } catch (Exception ex) {
                        f.accept(t, ex);
                        error.accept(ex);
                    }
                }, ex -> {
                    f.accept(null, ex);
                    error.accept(ex);
                });
            }
        };
    }

    void run(Consumer<A> ok, Consumer<Exception> error);
}
