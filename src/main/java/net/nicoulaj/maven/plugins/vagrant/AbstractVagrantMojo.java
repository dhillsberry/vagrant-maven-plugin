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
package net.nicoulaj.maven.plugins.vagrant;

import static java.util.Arrays.asList;
import static org.codehaus.plexus.util.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import net.nicoulaj.maven.plugins.vagrant.cmd.CommandExecutor;
import net.nicoulaj.maven.plugins.vagrant.cmd.ExecutionException;

/**
 * Base class for {@code Mojo}s invoking Vagrant.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @since 1.0
 */
abstract class AbstractVagrantMojo extends AbstractMojo {

	/**
	 * Custom {@code VAGRANT_HOME}, which is the directory where Vagrant boxes
	 * are stored.
	 * <p/>
	 * Modifying this property has an impact on isolation/build portability, eg:
	 * <ul>
	 * <li>In {@code project.build.directory} (default): boxes must be imported
	 * every time, but no file is created outside of build directory.</li>
	 * <li>In {@code project.basedir}: boxes can be imported once for all, but
	 * files are created outside of build directory.</li>
	 * <li>In {@code ~/.vagrant.d} (Vagrant default): user boxes can be directly
	 * used, but files are created outside of project structure.</li>
	 * </ul>
	 * 
	 * @parameter default-value="${project.build.directory}/vagrant/vagrant.d"
	 *
	 * @since 1.0
	 */
	protected File vagrantHome;

	/**
	 * Custom {@code VAGRANT_RC}, which is the configuration file used by
	 * Vagrant.
	 * <p/>
	 * Modifying this property has an impact on isolation/build portability, eg:
	 * <ul>
	 * <li>In {@code project.build.directory} (default): user installation can
	 * not impact build.</li>
	 * <li>In {@code project.basedir}: user installation can not impact build.
	 * </li>
	 * <li>In {@code ~/.vagrantrc} (Vagrant default): user installation can
	 * impact build.</li>
	 * </ul>
	 * 
	 * @parameter default-value="${project.build.directory}/vagrant/vagrantrc"*
	 * @since 1.0
	 */
	protected File vagrantRc;

	/**
	 * The <code>VAGRANT_HOME</code> environment variable name. An environment
	 * variable with this name will be passed through to the vagrant execution
	 */
	public static final String ENV_VAGRANT_HOME = "VAGRANT_HOME";

	/**
	 * the command used to execute vagrant
	 */
	public static final String VAGRANT_CMD = "vagrant";
	
	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {

		doExecute();

	}

	/**
	 * 
	 * @throws MojoExecutionException
	 */
	abstract protected void doExecute() throws MojoExecutionException;

	protected final void cli(String... args) throws MojoExecutionException {
		cli(asList(args));

	}

	/**
	 * 
	 * @param args
	 * @throws MojoExecutionException
	 */
	protected final void cli(Iterable<String> args) throws MojoExecutionException {
		ArrayList<String> command = new ArrayList<String>();
		for (String arg : args)
			if (isNotBlank(arg))
				command.add(arg);
		CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
		executor.setLogger(this.getLog());
		executor.setCaptureStdOut(true);
		executor.setCaptureStdErr(true);

		try{
			FileUtils.forceMkdir(vagrantHome);
			executor.addEnvironment(ENV_VAGRANT_HOME, vagrantHome.getAbsolutePath());
		} catch (IOException e) {
			getLog().error("Unable to use vagrant home",e);
			throw new MojoExecutionException("Unable to use vagrant home", e);
		}

		try {
			executor.executeCommand(VAGRANT_CMD, command, true);
		} catch (ExecutionException e) {
			getLog().error("Vagrant Command failed",e);
			throw new MojoExecutionException("Vagrant Command failed", e);
		}  finally {
			String errout = executor.getStandardError();
			if ((errout != null) && (errout.trim().length() > 0)) {
				getLog().error(errout);
			}
		}
	}

}
