/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package client;

import domain.utils.Constants;

public class HelloWorld {

	public static void main(String[] args) {
		int aNumber = 5;
		String courseID = "CANUJava";
		System.out.println("Hello " + courseID + " students to the Java World");
		System.out.println(aNumber);
		System.out.println("The solar luminosity is " + Constants.solarLuminosity + "erg/s");
	}

}
