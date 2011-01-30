﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using System.Text;

namespace cgl2files
{
    public class GPXParser
    {
        /* we only want to parse trk elements as that's the only output from CGL */
        public String LoadGPXTracks(string sFile)
        {
            XDocument gpxDoc = XDocument.Load(sFile);
            XNamespace gpx = XNamespace.Get("http://www.topografix.com/GPX/1/1");
            
            var tracks = from track in gpxDoc.Descendants(gpx + "trk")
                         select new
                         {
                             Name = track.Element(gpx + "name") != null ?
                              track.Element(gpx + "name").Value : null,
                             Segs = (
                                  from trackpoint in track.Descendants(gpx + "trkpt")
                                  select new
                                  {
                                      Latitude = trackpoint.Attribute("lat").Value,
                                      Longitude = trackpoint.Attribute("lon").Value,
                                      Elevation = trackpoint.Element(gpx + "ele") != null ?
                                        trackpoint.Element(gpx + "ele").Value : null,
                                      Time = trackpoint.Element(gpx + "time") != null ?
                                        trackpoint.Element(gpx + "time").Value : null
                                  }
                                )
                         };

            StringBuilder sb = new StringBuilder();

            foreach (var trk in tracks)
            {
                foreach (var trkSeg in trk.Segs)
                {
                    sb.Append(
                      string.Format("Track:{0} - Latitude:{1} Longitude:{2} " +
                                   "Elevation:{3} Date:{4}\n",
                      trk.Name, trkSeg.Latitude,
                      trkSeg.Longitude, trkSeg.Elevation,
                      trkSeg.Time));

                }
            }

            return sb.ToString();
        }

        public PositionDataSet LoadGPXTracksV2(string sFile)
        {
            XDocument gpxDoc = XDocument.Load(sFile);
            XNamespace gpx = XNamespace.Get("http://www.topografix.com/GPX/1/1");

            var tracks = from track in gpxDoc.Descendants(gpx + "trk")
                         select new
                         {
                             Name = track.Element(gpx + "name") != null ?
                              track.Element(gpx + "name").Value : null,
                             Segs = (
                                  from trackpoint in track.Descendants(gpx + "trkpt")
                                  select new
                                  {
                                      Latitude = trackpoint.Attribute("lat").Value,
                                      Longitude = trackpoint.Attribute("lon").Value,
                                      Elevation = trackpoint.Element(gpx + "ele") != null ?
                                        trackpoint.Element(gpx + "ele").Value : null,
                                      Time = trackpoint.Element(gpx + "time") != null ?
                                        trackpoint.Element(gpx + "time").Value : null
                                  }
                                )
                         };

            PositionDataSet dset = new PositionDataSet();
            
            foreach (var trk in tracks)
            {
                foreach (var trkSeg in trk.Segs)
                {
                    // only add tracks with time information
                    if (trkSeg.Time != null)
                    {
                        try
                        {
                            // convert string (ISO8601) to C# datetime object
                            DateTime time = DateTime.Parse(trkSeg.Time);
                            dset.addElem(time, trkSeg.Latitude, trkSeg.Longitude, trkSeg.Elevation);
                        }
                        catch (Exception)
                        {
                            Console.WriteLine("Unable to detect time format of trackSeg");
                        }

                    }
                }
            }

            return dset;
        }
    }
}
