package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class SerialPortFinder {
    private static final String TAG = "SerialPort";
    private Vector<Driver> mDrivers = null;

    public SerialPortFinder() {
    }

    Vector<Driver> getDrivers() throws IOException {
        if (null == this.mDrivers) {
            this.mDrivers = new Vector();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));

            String l;
            while (null != (l = r.readLine())) {
                String drivername = l.substring(0, 21).trim();
                String[] w = l.split(" +");
                if (w.length >= 5 && w[w.length - 1].equals("serial")) {
                    Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                    this.mDrivers.add(new Driver(drivername, w[w.length - 4]));
                }
            }

            r.close();
        }

        return this.mDrivers;
    }

    public String[] getAllDevices() {
        Vector devices = new Vector();

        try {
            Iterator itdriv = this.getDrivers().iterator();

            while (itdriv.hasNext()) {
                Driver driver = (Driver) itdriv.next();
                Iterator itdev = driver.getDevices().iterator();

                while (itdev.hasNext()) {
                    String device = ((File) itdev.next()).getName();
                    String value = String.format("%s (%s)", new Object[]{device, driver.getName()});
                    devices.add(value);
                }
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return (String[]) devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath() {
        Vector devices = new Vector();

        try {
            Iterator itdriv = this.getDrivers().iterator();

            while (itdriv.hasNext()) {
                Driver driver = (Driver) itdriv.next();
                Iterator itdev = driver.getDevices().iterator();

                while (itdev.hasNext()) {
                    String device = ((File) itdev.next()).getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return (String[]) devices.toArray(new String[devices.size()]);
    }

    public static class Driver {
        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public Driver(String name, String root) {
            this.mDriverName = name;
            this.mDeviceRoot = root;
        }

        public Vector<File> getDevices() {
            if (this.mDevices == null) {
                this.mDevices = new Vector();
                File dev = new File("/dev");
                File[] files = dev.listFiles();

                for (int i = 0; i < files.length; ++i) {
                    if (files[i].getAbsolutePath().startsWith(this.mDeviceRoot)) {
                        Log.d("SerialPort", "Found new device: " + files[i]);
                        this.mDevices.add(files[i]);
                    }
                }
            }

            return this.mDevices;
        }

        public String getName() {
            return this.mDriverName;
        }
    }
}
