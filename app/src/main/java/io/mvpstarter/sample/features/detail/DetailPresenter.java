package io.mvpstarter.sample.features.detail;

import javax.inject.Inject;

import io.mvpstarter.sample.data.DataManager;
import io.mvpstarter.sample.data.model.Statistic;
import io.mvpstarter.sample.features.base.BasePresenter;
import io.mvpstarter.sample.injection.ConfigPersistent;
import io.mvpstarter.sample.util.rx.scheduler.SchedulerUtils;

@ConfigPersistent
public class DetailPresenter extends BasePresenter<DetailMvpView> {

    private final DataManager mDataManager;

    @Inject
    public DetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DetailMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getPokemon(String name) {
        checkViewAttached();
        getMvpView().showProgress(true);
        mDataManager
                .getPokemon(name)
                .compose(SchedulerUtils.ioToMain())
                .subscribe(
                        pokemon -> {
                            // It should be always checked if MvpView (Fragment or Activity) is attached.
                            // Calling showProgress() on a not-attached fragment will throw a NPE
                            // It is possible to ask isAdded() in the fragment, but it's better to ask in the presenter
                            getMvpView().showProgress(false);
                            getMvpView().showPokemon(pokemon);
                            for (Statistic statistic : pokemon.stats) {
                                getMvpView().showStat(statistic);
                            }
                        },
                        throwable -> {
                            getMvpView().showProgress(false);
                            getMvpView().showError(throwable);
                        });
    }
}
