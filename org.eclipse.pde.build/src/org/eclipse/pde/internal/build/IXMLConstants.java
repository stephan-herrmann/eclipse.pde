/**********************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.pde.internal.build;
/**
 * XML template constants.
 */
public interface IXMLConstants {

	// general
	public static final String DEFAULT_FILENAME_SRC = ".src.zip";
	public static final String DEFAULT_FILENAME_LOG = ".log.zip";
	public static final String PROPERTY_ASSIGNMENT_PREFIX = "${";
	public static final String PROPERTY_ASSIGNMENT_SUFFIX = "}";
	public static final String PROPERTY_JAR_SUFFIX = ".jar";
	public static final String PROPERTY_SOURCE_PREFIX = "source.";
	public static final String PROPERTY_ZIP_SUFFIX = ".zip";
	public static final String JDT_COMPILER_ADAPTER = "org.eclipse.jdt.core.JDTCompilerAdapter";


	// element description variables (used in files like plugin.xml, e.g. $ws$)
	public static final String DESCRIPTION_VARIABLE_NL = "$nl$";
	public static final String DESCRIPTION_VARIABLE_OS = "$os$";
	public static final String DESCRIPTION_VARIABLE_WS = "$ws$";
	

	// targets
	public static final String TARGET_ALL_CHILDREN = "all.children";
	public static final String TARGET_ALL_FRAGMENTS = "all.fragments";
	public static final String TARGET_ALL_PLUGINS = "all.plugins";
	public static final String TARGET_BUILD_JARS = "build.jars";
	public static final String TARGET_BUILD_SOURCES = "build.sources";
	public static final String TARGET_BUILD_UPDATE_JAR = "build.update.jar";
	public static final String TARGET_BUILD_ZIPS = "build.zips";
	public static final String TARGET_CHILDREN = "children";
	public static final String TARGET_CLEAN = "clean";
	public static final String TARGET_FETCH = "fetch";
	public static final String TARGET_GATHER_BIN_PARTS = "gather.bin.parts";
	public static final String TARGET_GATHER_LOGS = "gather.logs";
	public static final String TARGET_GATHER_SOURCES = "gather.sources";
	public static final String TARGET_INIT = "init";
	public static final String TARGET_INSTALL = "install";
	public static final String TARGET_MAIN = "main";
	public static final String TARGET_PROPERTIES = "properties";
	public static final String TARGET_REFRESH = "refresh";
	public static final String TARGET_SRC_GATHER_WHOLE = "src.gather.whole";
	public static final String TARGET_TARGET = "target";
	public static final String TARGET_ZIP_DISTRIBUTION = "zip.distribution";
	public static final String TARGET_ZIP_LOGS = "zip.logs";
	public static final String TARGET_ZIP_PLUGIN = "zip.plugin";
	public static final String TARGET_ZIP_SOURCES = "zip.sources";
	
	// properties
	public static final String PROPERTY_ARCH = "arch";
	public static final String PROPERTY_BASEDIR = "basedir";
	public static final String PROPERTY_BIN_EXCLUDES = "bin.excludes";
	public static final String PROPERTY_BIN_INCLUDES = "bin.includes";
	public static final String PROPERTY_BOOTCLASSPATH = "bootclasspath";
	public static final String PROPERTY_BUILD_COMPILER = "build.compiler";
	public static final String PROPERTY_BUILD_ID = "build.id";
	public static final String PROPERTY_BUILD_QUALIFIER = "build.qualifier";
	public static final String PROPERTY_BUILD_RESULT_FOLDER = "build.result.folder";
	public static final String PROPERTY_BUILD_TYPE = "build.type";
	public static final String PROPERTY_CUSTOM = "custom";
	public static final String PROPERTY_DESTINATION_TEMP_FOLDER = "destination.temp.folder";
	public static final String PROPERTY_ECLIPSE_RUNNING = "eclipse.running";
	public static final String PROPERTY_FEATURE = "feature";
	public static final String PROPERTY_FEATURE_BASE = "feature.base";
	public static final String PROPERTY_FEATURE_DESTINATION = "feature.destination";
	public static final String PROPERTY_FEATURE_FULL_NAME = "feature.full.name";
	public static final String PROPERTY_FEATURE_TEMP_FOLDER = "feature.temp.folder";
	public static final String PROPERTY_FEATURE_VERSION_SUFFIX = "feature.version.suffix";
	public static final String PROPERTY_FULL_NAME = "full.name";
	public static final String PROPERTY_INCLUDE_CHILDREN = "include.children";
	public static final String PROPERTY_INSTALL = "install";
	public static final String PROPERTY_INSTALL_LOCATION = "install.location";
	public static final String PROPERTY_JAR_EXTRA_CLASSPATH = "jars.extra.classpath";
	public static final String PROPERTY_JAR_ORDER = "jars.compile.order";
	public static final String PROPERTY_NL = "nl";
	public static final String PROPERTY_OS = "os";
	public static final String PROPERTY_QUIET = "quiet";
	public static final String PROPERTY_SRC_EXCLUDES = "src.excludes";
	public static final String PROPERTY_SRC_INCLUDES = "src.includes";
	public static final String PROPERTY_PLUGIN_DESTINATION = "plugin.destination";
	public static final String PROPERTY_TARGET = "target";
	public static final String PROPERTY_TEMP_FOLDER = "temp.folder";
	public static final String PROPERTY_VERSION_SUFFIX = "version.suffix";
	public static final String PROPERTY_WS = "ws";
}