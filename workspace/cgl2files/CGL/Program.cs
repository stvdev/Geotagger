using System;
using System.IO;
using System.Drawing;
using System.Drawing.Imaging;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;


namespace cgl2files
{
    class Program
    {
        // following constant values are obtaind from the GDI+ const list:
        // http://msdn.microsoft.com/en-us/library/ms534416(v=VS.85).aspx
        const int PropertyTagGpsLatitude    = 0x2;
        const int PropertyTagGpsLongitude   = 0x4;
        const int PropertyTagGpsAltitude    = 0x6;
        const int PropertyTagGpsGpsTime     = 0x7;
        const int PropertyTagDateTime       = 0x132;
        const int PropertyTagExifDTDigitized = 0x9004;

        static void Main(string[] args)
        {
            
            if (args.Count() != 2)
            {
                Console.WriteLine("Invalid argument(s) provided\n");
                Console.WriteLine("Usage: CGL <path to GPX> <path to directory with pictures>");
                Console.ReadKey();
                return;
            }

            DirectoryInfo imgDir = new DirectoryInfo(@args[1]);
            FileInfo[] imgFiles = imgDir.GetFiles();
            Console.WriteLine("Following files were found");
            CultureInfo provider = CultureInfo.InvariantCulture;
            DateTime t = new DateTime(1970, 1, 1);

            GPXParser gpxParser = new GPXParser();
            PositionDataSet posDataSet = gpxParser.LoadGPXTracks(@args[0]);

            foreach (FileInfo img in imgFiles) {
                Console.WriteLine("File name:\t{0}, {1}", img.Name, img.FullName);
                try
                {
                    Image imgfile = Image.FromFile(@img.FullName);
                    //PropertyItem propItem = imgfile.GetPropertyItem(PropertyTagDateTime);
                    PropertyItem propItem = imgfile.GetPropertyItem(PropertyTagExifDTDigitized);
                    String date = System.Text.Encoding.ASCII.GetString(propItem.Value).Replace("\0", "");

                    try
                    {
                        t = DateTime.ParseExact(date, "yyyy:MM:dd HH:mm:ss", provider);

                    }
                    catch (System.FormatException)
                    {
                        Console.WriteLine("date format exception");
                    }

                    try
                    {
                        
                        PositionDataElem pos = posDataSet.findClosestTime(t);
                        if (pos.altitude != "-9999.99")
                        {
                            Console.WriteLine("applying geotag with following information:\n" +
                                "lat={0}, long={1}, alt={2}",
                                pos.latitude, pos.longitude, pos.altitude);

                            // set latitude
                            propItem.Id = PropertyTagGpsLatitude;
                            propItem.Value = System.Text.Encoding.UTF8.GetBytes(pos.latitude + "\0");
                            imgfile.SetPropertyItem(propItem);
                            
                            
                            // set longitude
                            propItem.Id = PropertyTagGpsLongitude;
                            propItem.Value = System.Text.Encoding.UTF8.GetBytes(pos.longitude + "\0");
                            imgfile.SetPropertyItem(propItem);
                            
                            // set altitude
                            propItem.Id = PropertyTagGpsAltitude;
                            propItem.Value = System.Text.Encoding.UTF8.GetBytes(pos.altitude + "\0");
                            imgfile.SetPropertyItem(propItem);
                            
                            try
                            {
                                String[] fname = @img.FullName.Split('.');
                                imgfile.Save(@fname[0] +"_geotagged." + fname[1], ImageFormat.Jpeg);
                            }
                            catch (Exception)
                            {
                                Console.WriteLine("failed to write to file");
                            }

                            Console.WriteLine("Geotagged: {0}\n\n", img.FullName);

                        }
                        else
                        {
                            Console.WriteLine("No geotagging of: {0}\n\n", img.FullName);
                        }
                    }
                    catch (Exception)
                    {
                        Console.WriteLine("Unable to find position");
                    }
                }
                catch (Exception)
                {
                    Console.WriteLine("Tag not found");
                }
            }

        }
    }
}
