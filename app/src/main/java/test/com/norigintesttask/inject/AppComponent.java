package test.com.norigintesttask.inject;

import javax.inject.Singleton;

import dagger.Component;
import test.com.norigintesttask.App;

@Singleton
@Component(modules = {
        AppModule.class,
        NetworkModule.class
})
public interface AppComponent {
    void inject(App app);
}
