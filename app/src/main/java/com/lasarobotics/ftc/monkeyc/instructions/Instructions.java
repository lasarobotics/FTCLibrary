package com.lasarobotics.ftc.monkeyc.instructions;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.apache.http.util.ByteArrayBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by arthur on 7/5/15.
 */
public abstract class Instructions {
    static final int MAX_LENGTH = 125;

    static enum Actions
    {
        NONE(0x00),
        STOP(0x01),
        WAIT_FOREVER(0x10),
        WAIT_TIME(0x11),
        WAIT_GYRO(0x12),
        MOVE_MOTOR(0x30),
        MOVE_MOTOR_WAIT(0x31),      //waits until next instruction to return true - should be a WAIT_* instruction
        MOVE_SERVO(0x32);

        int id;
        Actions(int id) { this.id = id; }
        int getInteger() { return id; }
        byte getByte() { return (byte)id; }
    }

    /**** BASIC OPERATIONS ****/

    public static byte[] noOperation() {
        return new byte[]{Actions.NONE.getByte()};
    }

    public static byte[] endProgram() {
        return new byte[]{Actions.STOP.getByte()};
    }

    /**** TIMING AND CONDITIONS ****/

    public static byte[] waitForever() {
        return new byte[]{Actions.WAIT_FOREVER.getByte()};
    }

    public static byte[] waitTime(long ms) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);

        buffer.put(Actions.WAIT_TIME.getByte());
        buffer.putLong(ms);

        buffer.compact();
        return buffer.array();
    }

    /**** BASIC MOTORS ****/

    public static byte[] moveMotor(String motor, double power)
    {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);

        buffer.put(Actions.MOVE_MOTOR.getByte());   //ID
        buffer = Utils.putString(buffer, motor);    //MOTOR
        buffer.putDouble(power);                    //POWER

        buffer.compact();
        return buffer.array();
    }

    public static byte[] moveMotor_Time(String motor, double power, long timems)
    {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);

        //write the motor instruction
        buffer.put(_moveMotor_Wait(motor, power));

        //write the condition
        buffer.put(waitTime(timems));

        buffer.compact();
        return buffer.array();
    }

    static byte[] _moveMotor_Wait(String motor, double power)
    {
        return _moveMotor_Wait(motor, power, 1, true);
    }

    static byte[] _moveMotor_Wait(String motor, double power, int conditions, boolean isOR)
    {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);
        buffer.put(Actions.MOVE_MOTOR_WAIT.getByte());          //ID
        buffer = Utils.putString(buffer, motor);                //MOTOR
        buffer.putDouble(power);                                //POWER
        buffer.put((byte) conditions);                          //CONDITION COUNTS
        buffer.put((byte)(isOR ? 1 : 0));                       //OR OPERATION?  True: OR on conditions, False: AND

        buffer.compact();
        return buffer.array();
    }
}