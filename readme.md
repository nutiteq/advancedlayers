# About

This is a set of free and open source map layer and data source implementations in top of Nutiteq Maps SDK 2.x branch http://www.nutiteq.com. You can use it as a ready-made library, or modify sources according to your needs. License: MIT

NB! AdvancedLayers works only with Nutiteq SDK 2.x version branch only, and latest version from it is required. **It is NOT compatible with the newest Nutiteq SDK 3.x version.**

# Getting started

 * Guides: https://github.com/nutiteq/hellomap3d/wiki for guides. 
 * Sample app code: https://github.com/nutiteq/hellomap3d/tree/master/AdvancedMap3D
 * Running AdvancedMap3D sample app: https://github.com/nutiteq/hellomap3d/wiki/Get-advancedmap3d-to-start 

# Usage in your app

To use the library in your app you need to include AdvancedLayers and external dependencies to your project. Depending on your development environment and IDE there are several ways to do it.

## In Android Studio / Gradle

Into your **build.gradle**

    // Make sure you have nutiteq snapshot repository:

    allprojects {
        repositories {
            mavenCentral()
            maven {
                url "http://repository-nutiteq.forge.cloudbees.com/release/"
		            }
            maven {
                url "http://repository-nutiteq.forge.cloudbees.com/snapshot/"
                }

        }
    }


    // add advancedlayers dependency:
    dependencies {
        compile "com.nutiteq:nutiteq-3d-sdk:2.4.0"
        compile "com.nutiteq.advancedlayers:AdvancedLayers:1.0-SNAPSHOT"
    }


## Maven (Eclipse)

Add to your **pom.xml** (full sample: https://github.com/nutiteq/hellomap3d/blob/master/AdvancedMap3D/pom.xml):

	<repositories>
		<repository>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
			</snapshots>
			<id>cloudbees-nutiteq-snapshot</id>
			<name>Nutiteq SDK snapshot repository</name>
			<url>http://repository-nutiteq.forge.cloudbees.com/snapshot/</url>
		</repository>
		<repository>
			<id>cloudbees-nutiteq-release</id>
			<name>Nutiteq SDK release repository</name>
			<url>http://repository-nutiteq.forge.cloudbees.com/release/</url>
		</repository>
	</repositories>
	
	<dependencies>
		<dependency>
			<groupId>com.google.android</groupId>
			<artifactId>android</artifactId>
			<version>${platform.version}</version>
			<scope>provided</scope>
		</dependency>

		<!-- Advanced layers and Nutiteq SDK -->
		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>AdvancedLayers</artifactId>
			<version>1.0-SNAPSHOT</version>
			<scope>compile</scope>
		</dependency>

		<!-- Native (NDK) libraries built by Nutiteq -->
		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>ogrjni</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>gdalconstjni</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>gdaljni</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>jsqlite</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>osrjni</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

		<dependency>
			<groupId>com.nutiteq.advancedlayers</groupId>
			<artifactId>proj</artifactId>
			<version>snapshot</version>
			<classifier>armeabi</classifier>
			<type>so</type>
		</dependency>

## Manual jar file

1. Download Nutiteq code:
  * Latest .jar file from https://github.com/nutiteq/hellomap3d/blob/master/AdvancedMap3D/extlibs/AdvancedLayers-1.0-SNAPSHOT.jar 
  * Latest Nutiteq SDK jar file from https://github.com/nutiteq/hellomap3d/wiki/Downloads

2. Save them to your project libs/ folder. 

3. For other dependencies: 

 * Java libs from extlibs folder (https://github.com/nutiteq/advancedlayers/tree/master/extlibs)
 * If you use native libraries (for e.g. sqlite, shapefile, geotiff etc) then you need also .so files from same location, usually armeabi/ folder is enough.

# Test datasets

Depending on layers you may find useful to copy following files to the sdcard of your device, and modify paths in the code accordingly:

* Spatialite : Romania OpenStreetMap data, Spatialite 3.0 format: https://www.dropbox.com/s/j4aahjo7whkzx2r/romania_sp3857.sqlite
* Raster data: Digital Earth, Natural earth converted to Spherical Mercator: https://www.dropbox.com/s/fwd7f1l4gy36u94/natural-earth-2-mercator.tif
* Mapsforge: http://ftp.mapsforge.org/maps
* Shapefiles: OpenStreetMap data, Estonia: https://www.dropbox.com/s/72yhmo2adl01dho/shp_ee_3857.zip
* MBTiles: European countries with UTFGrid interaction: http://a.tiles.mapbox.com/v3/nutiteq.geography-class.mbtiles . Or you can download free TileMill http://mapbox.com/tilemill/ and create a package yourself.
