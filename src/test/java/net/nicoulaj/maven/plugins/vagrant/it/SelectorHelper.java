/*
 * Copyright 2013 vagrant-maven-plugin contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nicoulaj.maven.plugins.vagrant.it;

import org.codehaus.plexus.util.IOUtil;

import java.io.*;

/**
 * Utilities used by maven-invoker-plugin selector scripts.
 * <p/>
 * <p>See {@code src/main/it/projects/.../selector.bsh} scripts.</p>
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @since 1.0
 */
public class SelectorHelper {

		/** @return {@code true} if VirtualBox is installed. */
		public static boolean isVirtualBoxAvailable() {
			return isVirtualBoxAvailableWindows() || isVirtualBoxAvailableUnix();
		}

    /** @return {@code true} if VirtualBox is installed. */
    public static boolean isVirtualBoxAvailableWindows() {
	      return checkAvailability("VBoxManage --help", "VirtualBox Command Line Management Interface");
    }

		/** @return {@code true} if VirtualBox is installed. */
		public static boolean isVirtualBoxAvailableUnix() {
				return checkAvailability("vboxmanage --help", "VirtualBox Command Line Management Interface");
		}

		private static boolean checkAvailability(String command, String expectedResponse) {

			InputStream is = null;
			try {
				is = Runtime.getRuntime().exec(command).getInputStream();
				String fullConsole = IOUtil.toString(is);
				System.out.println(fullConsole);
				return fullConsole.contains(expectedResponse);
			} catch (Throwable t) {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				return false;
			}

		}
}
