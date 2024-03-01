package com.skt.common.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class SecurityEscape {
    public static String cleanIt(String arg0) {
        return Jsoup.clean(arg0, Safelist.none());
    }
}
