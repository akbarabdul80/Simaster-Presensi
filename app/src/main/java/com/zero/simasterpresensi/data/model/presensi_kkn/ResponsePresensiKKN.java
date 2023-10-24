package com.zero.simasterpresensi.data.model.presensi_kkn;

import pl.droidsonroids.jspoon.annotation.Selector;

public class ResponsePresensiKKN {
    @Selector(".note")
    public String note;
}