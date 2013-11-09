package org.team3309.frc2013;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

public class Arm {

	private static Arm instance;

	public static Arm getInstance() {
		if (instance == null) {
			instance = new Arm();
		}
		return instance;
	}

	private Victor tilt1, tilt2;
	private Victor shoot1, shoot2, shoot3;
	private Victor intake;
	private Solenoid loader;

	private Arm() {
		intake = new Victor(RobotMap.ARM_INTAKE);
		tilt1 = new Victor(RobotMap.ARM_TILT1);
		tilt2 = new Victor(RobotMap.ARM_TILT2);
		shoot1 = new Victor(RobotMap.SHOOT1);
		shoot2 = new Victor(RobotMap.SHOOT2);
		shoot3 = new Victor(RobotMap.SHOOT3);
		loader = new Solenoid(RobotMap.SHOOT_LOADER);
	}

	public void setIntake(double speed) {
		intake.set(speed);
	}

	public void tilt(double speed) {
		tilt1.set(speed);
		tilt2.set(-speed);
	}

	public void setShooter(double speed) {
		shoot1.set(speed);
		shoot2.set(speed);
		shoot3.set(speed);
	}
	
	public void shoot(){
		extendLoader();
		Timer.delay(.1);
		retractLoader();
	}
	
	public void extendLoader(){
		loader.set(true);
	}
	
	public void retractLoader(){
		loader.set(false);
	}

}
