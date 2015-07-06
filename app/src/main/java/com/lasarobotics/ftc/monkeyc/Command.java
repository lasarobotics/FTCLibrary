package com.lasarobotics.ftc.monkeyc;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.nio.ByteBuffer;
import java.lang.reflect.Method;

/**
 * Command available for the MonkeyC protocol
 */
public abstract class Command {
    static final int MAX_LENGTH = 125;

    protected enum Actions
    {
        NONE(0x00, "noOperation"),
        STOP(0x01, "endProgram"),
        WAIT_FOREVER(0x10, "waitForever"),
        WAIT_TIME(0x11, "waitTime"),
        WAIT_GYRO(0x12, null),
        MOVE_MOTOR(0x30, "moveMotor"),
        MOVE_SERVO(0x31, null);

        int id;
        Method m;
        Actions(int id, String method) {
            this.id = id;
            try {
                this.m = Command.class.getMethod(method, MonkeyDo.class);
            } catch (NoSuchMethodException e)
            {
                this.m = null;
            }
        }
        byte getByte() { return (byte)id; }
    }

    /**** BASIC OPERATIONS ****/

    public static byte[] noOperation() {
        return new byte[]{Actions.NONE.getByte()};
    }
    static void noOperation(MonkeyDo monkey) { }

    public static byte[] endProgram() {
        return new byte[]{Actions.STOP.getByte()};
    }
    static void endProgram(MonkeyDo monkey)
    {
        monkey.mode.stop();
    }

    /**** TIMING AND CONDITIONS ****/

    public static byte[] waitForever() {
        return new byte[]{Actions.WAIT_FOREVER.getByte()};
    }
    static void waitForever(MonkeyDo monkey)
    {
        do {
            try {
                monkey.wait(1000);
            } catch (InterruptedException e)
            {

            }
        } while (true);
    }


    public static byte[] waitTime(long ms) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);

        buffer.put(Actions.WAIT_TIME.getByte());
        buffer.putLong(ms);

        buffer.compact();
        return buffer.array();
    }
    static void waitTime(MonkeyDo monkey) {
        long ms = monkey.instruction.getLong();

        try {
            monkey.wait(ms);
        } catch (InterruptedException e)
        {

        }
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
    static void moveMotor(MonkeyDo monkey)
    {
        String name = Utils.getString(monkey);          //MOTOR NAME
        DcMotor motor = monkey.map.dcMotor.get(name);
        double power = monkey.instruction.getDouble();  //POWER

        motor.setPower(power);
    }

    public static byte[] moveMotor_Time(String motor, double power, long timems)
    {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_LENGTH);

        //write the motor instruction
        buffer.put(moveMotor(motor, power));

        //write the condition
        buffer.put(waitTime(timems));

        buffer.compact();
        return buffer.array();
    }
}