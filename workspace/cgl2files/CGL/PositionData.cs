using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace cgl2files
{
    public class PositionDataElem
    {
        public double date { set; get; }
        public String latitude { set; get; }
        public String longitude { set; get; }
        public String altitude { set; get; }     
    }

    public class PositionDataSet
    {
        private List<PositionDataElem> data;
        // acceptable time offset 1h 
        private const double acceptTimeOffset = 30.0;

        public PositionDataSet() { data = new List<PositionDataElem>(); }

        public void addElem(DateTime date, String lat, String lon, String alt)
        {
            data.Add(
                new PositionDataElem() {
                    date = (date - new DateTime(1970, 1, 1)).TotalSeconds,
                    latitude= lat,
                    longitude = lon,
                    altitude = alt
                }
            );
        }

        public PositionDataElem findClosestTime(DateTime date)
        {
            double missy = 0.0;
            PositionDataElem invalid = new PositionDataElem() { date = 0.0, latitude = "0.000", longitude = "0.000", altitude = "-9999.99" };
            PositionDataElem prev = invalid;

            Console.WriteLine("Searching for position near time {0}", date);

            try{
                missy = (date - new DateTime(1970, 1, 1)).TotalSeconds;
            }
            catch(Exception)
            {
                Console.WriteLine("failed for parse date");
                return prev;
            }

            try
            {
                data.OrderBy(x => x.date);
            }
            catch (Exception)
            {
                Console.WriteLine("Failed to orderBY");
                return prev;
            }
            
            foreach (PositionDataElem curr in data)
            {
  
                // boundary search
                if(missy > curr.date)
                {
                    // determine which position is closest

                    if ((missy - curr.date) > (missy - prev.date))
                    {
                        // old might be closer to curr, so lets do a check
                        // curr - old < lim
                        if (missy - prev.date < acceptTimeOffset)
                        {
                            return prev;
                        }
                    }
                }
                else if (curr.date - missy < acceptTimeOffset)
                {
                    return curr;
                }
                prev = curr;
            }

            return invalid;
        }


        public List<PositionDataElem> getPositionData()
        {
            return data;
        }

    }

}
