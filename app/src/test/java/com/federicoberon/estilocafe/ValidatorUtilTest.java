package com.federicoberon.estilocafe;

import org.junit.Test;

import junitparams.Parameters;

import static org.junit.Assert.*;

import com.federicoberon.estilocafe.utils.ValidatorUtil;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ValidatorUtilTest {

    private static final Object[] getInvalidText() {
        return new Object[] {
                new Object[] {"123 Titulo que comienza con numero"},
                new Object[] {"123456"},
                new Object[] {""},
                new Object[] {"a"},
                new Object[] {"Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."},
                new Object[] {"Con simbolos @"},
                new Object[] {"Con simbolos {}"},
                new Object[] {"Con simbolos ()"},
                new Object[] {"Con simbolos ##"},
                new Object[] {"Con simbolos ,"},
                new Object[] {"Con simbolos :"}
        };
    }

    private static final Object[] getValidText() {
        return new Object[] {
                new Object[] {"Titulo sin numeros"},
                new Object[] {"Titulo con 4lgunos num3r0s 123"},
                new Object[] {"Con palabras acentuadas melón"}
        };
    }

    private static final Object[] getValidEmail() {
        return new Object[] {
                new Object[] {"abc@abc.com"},
                new Object[] {"abc12_@asd.com.ar"}
        };
    }

    private static final Object[] getInvalidEmail() {
        return new Object[] {
                new Object[] {"123@abc.com"},
                new Object[] {"abc.com."},
                new Object[] {"abc@abc@abc.com*"},
                new Object[] {"Solo texto"},
                new Object[] {"abc@abc"}
        };
    }

    @Test
    @Parameters(method = "getValidText")
    public void test_IsValidText(String input) {
        assertTrue(ValidatorUtil.isValidText(input));
    }

    @Test
    @Parameters(method = "getInvalidText")
    public void test_IsInvalidText(String input) {
        assertFalse(ValidatorUtil.isValidText(input));
    }

    @Test
    @Parameters(method = "getValidEmail")
    public void test_IsValidEmail(String input) {
        assertTrue(ValidatorUtil.isValidEmail(input));
    }

    @Test
    @Parameters(method = "getInvalidEmail")
    public void test_IsInvalidEmail(String input) {
        assertFalse(ValidatorUtil.isValidEmail(input));
    }
}