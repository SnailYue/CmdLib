package com.ebanswers.cmdlib.exception;

import java.io.IOException;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class ComException {

    /**
     * Command的init方法传入的字符串不是Assets目录下的文件异常
     */
    public static class AssetException extends IOException {
        private static final long serialVersionUID = 1L;

        public AssetException() {
            super();
        }

        public AssetException(String string) {
            super(string);
        }
    }
}
