package com.lasarobotics.library;

import com.lasarobotics.library.util.MathUtil;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;


/**
 * Basic unit tests
 */
public class MathUtilTest {

    @Test
    public void deadbandTest_ValueLessThanDedband_ReturnsZero() {
        assertThat(MathUtil.deadband(10, 5), is(0.0));
    }

    @Test
    public void deadbandTest_GreatLessThanDedband_ReturnsValue() {
        assertThat(MathUtil.deadband(10, 11), is(11.0));
    }
}