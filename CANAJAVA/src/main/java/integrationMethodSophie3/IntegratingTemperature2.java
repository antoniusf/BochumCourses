package integrationMethodSophie3;

import java.util.ArrayList;
import java.util.List;

import domain.utils.Constants;

public class IntegratingTemperature2 {

	double T_0 = 0; // initial temperature/luminosity at the surface
	double L_0 = 0;
	double X; // Mass fraction of hydrogen
	double Z; // Mass fraction of metal

	double e;
	double kappa;
	double k_bb;
	double k_bf;
	double k_es;
	double k_ff;
	double k_h;
	double t = 100; // guillotine factor, between 1 and 100
	double mu = 1; // mean molecular mass

	private List<IntegrationListener> myListeners;

	public IntegratingTemperature2() {
		this.myListeners = new ArrayList<>();
	}

	public void setT_0(double t_0) {
		T_0 = t_0;
	}

	public void setL_0(double l_0) {
		L_0 = l_0;
	}

	public void setX(double x) {
		X = x;
	}

	public void setZ(double z) {
		Z = z;
	}

	public void setGuillotineFactor(double g) {
		t = g;
	}

	public double EulersMethod(double r_0, double rho, int n) {

		double deltaR = r_0 / n;

		// at the surface: T=T_0, L=L_0:
		double T_n = T_0;
		double L_n = L_0;
		double T_np1 = T_0;
		double L_np1 = L_0;

		double P_n = 0;
		double P_np1 = 0;
		double rho_n = 1e-12;

		double r = r_0;

		// constants of luminosity and temperature:
		double c_L = 4 * Math.PI;
		double c_T = (3) / (16 * Math.PI * 4 * 5.67 * 1e-8); // sigma = 5.67 * 1e-8 W/(m^2K^4) Stefan-Boltzmann
																// Constant
		double c_p = 4 * Math.PI / 3 * Constants.gravitationalConstant;
		for (int i = 0; i < n + 1; i++) {

			// released energy in pp-reaction, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p.311
			e = 0.241 * rho_n * X * X * Math.pow(T_n / (1e6), -2 / 3) * Math.exp(-33.8 * Math.pow(T_n / (1e6), -1 / 3));

			// Rossland mean opacity, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p. 249-250

			k_bb = 4.34 * 1e21 * Z / t * (1 + X) * rho_n / (Math.pow(T_n, 3.5));
			k_bf = 3.68 * 1e18 * (1 - Z) * (1 + X) * rho_n / (Math.pow(T_n, 3.5));
			k_es = 0.02 * (1 + X);
			k_h = 7.9 * 1e-34 * (Z / 0.02) * Math.sqrt(rho_n) * Math.pow(T_n, 9);

			kappa = k_bb + k_bf + k_ff + k_es + k_h; // m^2/kg

			L_np1 -= c_L * rho_n * r * r * e * deltaR;
			T_np1 += c_T * rho_n * kappa * L_n / (r * r * Math.pow(T_n, 3)) * deltaR;

			P_np1 += c_p * rho_n * rho_n * r * deltaR;

			// derive rho from ideal gas equation and radiation pressure:
			// with new Pressure, Temperature
			// An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p. 295
			// with radiation pressure:

			// rho_n = (P_np1 - 1 / 3 * 4 * 5.67 * 1e-8 / Constants.speedOfLight *
			// Math.pow(T_np1, 4)) * mu
			// * Constants.massOfHydogen / (T_np1 * 1.38 * 1e-23);

			// without radiation pressure:

			rho_n = P_np1 * mu * Constants.massOfHydogen / (T_np1 * 1.38 * 1e-23);

			notifyMyListeners((int) (i / n * 100), false);

			// System.out.println(r);
			// System.out.println(" L: " + L_np1);
			// System.out.println(" T: " + T_np1);
			// System.out.println(" P: " + P_np1);
			// System.out.println(" rho: " + rho_n);

			r -= deltaR;
			L_n = L_np1;
			T_n = T_np1;
			P_n = P_np1;

		}

		notifyMyListeners(100, true);

		return T_np1;
	}

	public double EulersMethod(double r_0, double rho, double l) {

		if (l > r_0) {
			System.err.println("Step size is larger than radius!");
		}
		int n = (int) (r_0 / l);
		double deltaR = r_0 / n;

		// at the surface: T=T_0, L=L_0:
		double T_n = T_0;
		double L_n = L_0;
		double T_np1 = T_0;
		double L_np1 = L_0;

		double P_n = 0;
		double P_np1 = 0;
		double rho_n = 0;
		double rho_np1 = 0.00001;

		double r = r_0;

		// constants of luminosity and temperature:
		double c_L = 4 * Math.PI;
		double c_T = (3) / (16 * Math.PI * 4 * 5.67 * 1e-8); // sigma = 5.67 * 1e-8 W/(m^2K^4) Stefan-Boltzmann
																// Constant
		double c_p = 4 * Math.PI / 3 * Constants.gravitationalConstant;

		for (int i = 0; i < n + 1; i++) {

			// derive rho from ideal gas equation and radiation pressure:
			rho_np1 += (P_n - 1 / 3 * 4 * 5.67 * 1e-8 / Constants.speedOfLight * Math.pow(T_n, 4)) * mu
					* Constants.massOfHydogen / (T_n * 1.38 * 1e-23);
			P_np1 += c_p * rho_n * rho_n * r * deltaR;

			// released energy in pp-reaction, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p.311
			e = 0.241 * rho_n * X * X * Math.pow(T_n / (1e6), -2 / 3) * Math.exp(-33.8 * Math.pow(T_n / (1e6), -1 / 3));

			// Rossland mean opacity, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p. 249-250

			k_bb = 4.34 * 1e21 * Z / t * (1 + X) * rho_n / (Math.pow(T_n, 3.5));
			k_bf = 3.68 * 1e18 * (1 - Z) * (1 + X) * rho_n / (Math.pow(T_n, 3.5));
			k_es = 0.02 * (1 + X);
			k_h = 7.9 * 1e-34 * (Z / 0.02) * Math.sqrt(rho_n) * Math.pow(T_n, 9);

			kappa = k_bb + k_bf + k_ff + k_es + k_h; // m^2/kg

			L_np1 -= c_L * rho_n * r * r * e * deltaR;
			T_np1 += c_T * rho_n * kappa * L_n / (r * r * Math.pow(T_n, 3)) * deltaR;

			notifyMyListeners((int) (i / n * 100), false);

			// System.out.println(r);
			// System.out.println(" L: " + L_np1);
			// System.out.println(" T: " + T_np1);
			// System.out.println(" P: " + P_np1);
			// System.out.println(" rho: " + rho_np1);

			r -= deltaR;
			L_n = L_np1;
			T_n = T_np1;
			rho_n = rho_np1;
			P_n = P_np1;
		}

		notifyMyListeners(100, true);

		return T_np1;
	}

	public double SimpsonsMethod(double r_0, double rho, int n) {

		double deltaR = r_0 / n;

		// at the surface: T=T_0, L=L_0:
		double T_n = T_0;
		double L_n = L_0;
		double T_np1 = T_0;
		double L_np1 = L_0;

		double r = r_0;

		// constants of luminosity and temperature:
		double c_L = 4 * Math.PI * rho * rho;
		double c_T = (3 * rho) / (16 * Math.PI * 4 * 5.67 * 1e-8); // sigma = 5.67 * 1e-8 W/(m^2K^4) Stefan-Boltzmann
																	// Constant

		for (int i = 0; i < n + 1; i++) {

			// released energy in pp-reaction, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p.311
			e = 0.241 * rho * X * X * Math.pow(T_n / (1e6), -2 / 3) * Math.exp(-33.8 * Math.pow(T_n / (1e6), -1 / 3));

			// Rossland mean opacity, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p. 249-250

			k_bb = 4.34 * 1e21 * Z / t * (1 + X) * rho / (Math.pow(T_n, 3.5));
			k_bf = 3.68 * 1e18 * (1 - Z) * (1 + X) * rho / (Math.pow(T_n, 3.5));
			k_es = 0.02 * (1 + X);
			k_h = 7.9 * 1e-34 * (Z / 0.02) * Math.sqrt(rho) * Math.pow(T_n, 9);

			kappa = k_bb + k_bf + k_ff + k_es + k_h; // m^2/kg

			if (i % 2 == 0) {

				L_np1 -= 2 * c_L * r * r * e * (r - i * deltaR);
				T_np1 += 2 * c_T * kappa * L_n / (r * r * Math.pow(T_n, 3)) * (r - i * deltaR);

				notifyMyListeners((int) (i / n * 100), false);

			} else {

				L_np1 -= 4 * c_L * r * r * e * (r - i * deltaR);
				T_np1 += 4 * c_T * kappa * L_n / (r * r * Math.pow(T_n, 3)) * (r - i * deltaR);

				notifyMyListeners((int) (i / n * 100), false);
			}
			// System.out.println(L_np1);
			// System.out.println(T_np1);

			r -= deltaR;
			L_n = L_np1;
			T_n = T_np1;

		}

		L_np1 = L_np1 * deltaR / 3.0;
		T_np1 = T_np1 * deltaR / 3.0;

		notifyMyListeners(100, true);

		return T_np1;
	}

	public double SimpsonsMethod(double r_0, double rho, double l) {

		if (l > r_0) {
			System.err.println("Step size is larger than radius!");
		}
		int n = (int) (r_0 / l);

		double deltaR = r_0 / n;

		// at the surface: T=T_0, L=L_0:
		double T_n = T_0;
		double L_n = L_0;
		double T_np1 = T_0;
		double L_np1 = L_0;

		double r = r_0;

		// constants of luminosity and temperature:
		double c_L = 4 * Math.PI * rho * rho;
		double c_T = (3 * rho) / (16 * Math.PI * 4 * 5.67 * 1e-8); // sigma = 5.67 * 1e-8 W/(m^2K^4) Stefan-Boltzmann
																	// Constant

		for (int i = 0; i < n + 1; i++) {

			// released energy in pp-reaction, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p.311
			e = 0.241 * rho * X * X * Math.pow(T_n / (1e6), -2 / 3) * Math.exp(-33.8 * Math.pow(T_n / (1e6), -1 / 3));

			// Rossland mean opacity, An Introduction to Modern Astrophysics,
			// Caroll&Ostlie, p. 249-250

			k_bb = 4.34 * 1e21 * Z / t * (1 + X) * rho / (Math.pow(T_n, 3.5));
			k_bf = 3.68 * 1e18 * (1 - Z) * (1 + X) * rho / (Math.pow(T_n, 3.5));
			k_es = 0.02 * (1 + X);
			k_h = 7.9 * 1e-34 * (Z / 0.02) * Math.sqrt(rho) * Math.pow(T_n, 9);

			kappa = k_bb + k_bf + k_ff + k_es + k_h; // m^2/kg

			if (i % 2 == 0) {

				L_np1 -= 2 * c_L * r * r * e * (r - i * deltaR);
				T_np1 += 2 * c_T * kappa * L_n / (r * r * Math.pow(T_n, 3)) * (r - i * deltaR);

				notifyMyListeners((int) (i * deltaR / r * 100), false);

			} else {

				L_np1 -= 4 * c_L * r * r * e * (r - i * deltaR);
				T_np1 += 4 * c_T * kappa * L_n / (r * r * Math.pow(T_n, 3)) * (r - i * deltaR);

				notifyMyListeners((int) (i * deltaR / r * 100), false);
			}

			// System.out.println(L_np1);
			// System.out.println(T_np1);

			r -= deltaR;
			L_n = L_np1;
			T_n = T_np1;

		}

		L_np1 = L_np1 * deltaR / 3.0;
		T_np1 = T_np1 * deltaR / 3.0;

		notifyMyListeners(100, true);

		return T_np1;
	}

	private void notifyMyListeners(int value, boolean finished) {

		IntegrationEvent event = new IntegrationEvent(value, finished);

		for (IntegrationListener IntegrationListener : myListeners) {
			IntegrationListener iEventListener = IntegrationListener;
			iEventListener.nextStep(event);
		}

	}

	public void addListener(IntegrationListener listener) {

		myListeners.add(listener);

	}

	public void removeListener(IntegrationListener listener) {

		myListeners.remove(listener);

	}

}
