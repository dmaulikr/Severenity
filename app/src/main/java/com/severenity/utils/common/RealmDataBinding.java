package com.severenity.utils.common;

import io.realm.RealmChangeListener;

/**
 * Created by Novosad on 5/31/17.
 */

public interface RealmDataBinding {
    interface Factory {
        RealmChangeListener create();
    }

    RealmDataBinding.Factory FACTORY = new Factory() {
        @Override
        public RealmChangeListener create() {
            return new RealmChangeListener() {
                @Override
                public void onChange(Object element) {
                    if (element instanceof RealmDataBinding) {
                        ((RealmDataBinding) element).notifyChange();
                    }
                }
            };
        }
    };

    void notifyChange();
}
