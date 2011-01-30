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

        static void Main(string[] args)
        {

            DirectoryInfo imgDir = new DirectoryInfo(@"C:\dev\pics");
            FileInfo[] imgFiles = imgDir.GetFiles();
            Console.WriteLine("Following files were found");
            CultureInfo provider = CultureInfo.InvariantCulture;
            DateTime t = new DateTime(1970, 1, 1);

            GPXParser gpxParser = new GPXParser();
            PositionDataSet posDataSet = gpxParser.LoadGPXTracksV2(@"C:\dev\path.xml");

            foreach (FileInfo img in imgFiles) {
                Console.WriteLine("File name:\t{0}, {1}", img.Name, img.FullName);
                try
                {
                    Image imgfile = Image.FromFile(@img.FullName);
                    PropertyItem propItem = imgfile.GetPropertyItem(PropertyTagDateTime);
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
                                imgfile.Save(@img.FullName + "tmp", ImageFormat.Jpeg);
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

            Console.ReadKey();
        }
    }
}
