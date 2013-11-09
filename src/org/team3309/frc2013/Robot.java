/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.team3309.frc2013;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team3309.frc2013.commands.ExtendRetractLoader;
import org.team3309.frc2013.commands.FakeCommand;
import org.team3309.frc2013.commands.ShootAuton;
import org.team3309.frc2013.commands.TrollAuton;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private Drive mDrive = null;
	private Arm mArm = null;
	private XboxController driverXbox = new XboxController(1);
	private XboxController operatorXbox = new XboxController(2);
	private Compressor compressor = null;
	private Scheduler scheduler;
	private SendableChooser autonChooser = new SendableChooser();

	private Latch extendLatch = new Latch();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		scheduler = Scheduler.getInstance();

		mDrive = Drive.getInstance();
		mArm = Arm.getInstance();

		compressor = new Compressor(RobotMap.PRESSURE_SWITCH,
				RobotMap.COMPRESSOR_RELAY);
		compressor.start();

		autonChooser.addObject("Do nothing", new FakeCommand());
		SmartDashboard.putData("Autonomous Chooser", autonChooser);
	}

	public void disabledInit() {
		mDrive.resetGyro();
	}

	public void disabledPeriodic() {
	}

	public void autonomousInit() {
		mDrive.resetGyro();
		mDrive.enablePid();
		mDrive.resetEncoders();
		mDrive.driveStraight(0);

		mDrive.stop();
		mDrive.disengagePto();

		((Command) autonChooser.getSelected()).start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		scheduler.run();
	}

	public void teleopInit() {
		mDrive.resetGyro();
		mDrive.disablePid();

		mDrive.highGear();

		new JoystickButton(operatorXbox, XboxController.BUTTON_LEFT_BUMPER)
				.whenPressed(new ExtendRetractLoader());
	}

	private boolean climbingMode = false;
	// this stuff copied from
	// http://chiefdelphi.com/forums/showpost.php?p=1212189&postcount=3
	private double speed = 0; // use this for ramping the speed to make smoother
								// control
	private static final double MAX_SPEED_CHANGE = 0.05; // use this for ramping
															// the speed to make
															// smoother control

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
        scheduler.run();

        if (driverXbox.getStart()) {
            climbingMode = true;
            System.out.println("entering climbing mode");
            mDrive.engagePto();
        } else if (driverXbox.getBack()) {
            climbingMode = false;
            System.out.println("exiting climbing mode");
            mDrive.disengagePto();
        }

        if (!climbingMode) {
            double throttle = driverXbox.getLeftY();
            double turn = driverXbox.getRightX() + operatorXbox.getLeftX()*.25;

            if (throttle > (speed + MAX_SPEED_CHANGE)) {
                speed = speed + MAX_SPEED_CHANGE;
            } else if (throttle < (speed - MAX_SPEED_CHANGE)) {
                speed = speed - MAX_SPEED_CHANGE;
            } else {
                speed = throttle;
            }

            if (Math.abs(driverXbox.getRightTrigger()) > .25) {
                mDrive.lowGear();
                compressor.stop();
            } else {
                mDrive.highGear();
                compressor.start();
            }

            if(driverXbox.getLeftStickPressed())
                mDrive.disableGyro();
            else if(driverXbox.getRightStickPressed())
                mDrive.enableGyro();

            if (driverXbox.getAButton()) {
                mDrive.resetGyro();
                mDrive.resetEncoders();
            }
        }
        
        mArm.tilt(operatorXbox.getLeftY());
        mArm.setShooter(operatorXbox.getRightY());
        
        if(operatorXbox.getRightBumper())
        	mArm.shoot();
        if(operatorXbox.getAButton())
        	mArm.setIntake(.99);
        else if(operatorXbox.getBButton()){
        	mArm.setIntake(-.99);
        }
        

        /*double target = 0;

        if (operatorXbox.getYButton()) {
            target = Shooter.PYRAMID_TARGET_RPM;
        }
        if (operatorXbox.getStart()) {
            target = 8000;
        }
        if (operatorXbox.getRightBumper()) {
            mDrive.stop();
            if (mShooter.isTargetSpeed()) {
                mShooter.shoot();
                Timer.delay(.5);
            }
        }

        mShooter.setTargetRpm(target);
        if (target >= 2000) {
            compressor.stop();
        } else {
            compressor.start();
        }*/
    }

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
	}
}
