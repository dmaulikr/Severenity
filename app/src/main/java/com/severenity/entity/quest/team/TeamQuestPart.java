package com.severenity.entity.quest.team;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Novosad on 5/30/17.
 */
public class TeamQuestPart extends RealmObject {
    @Required
    private String placeId;

    @Required
    private String code;
    private RealmList<QuestTip> tips;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RealmList<QuestTip> getTips() {
        return tips;
    }

    public void setTips(RealmList<QuestTip> tips) {
        this.tips = tips;
    }
}
