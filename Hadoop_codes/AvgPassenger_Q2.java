import java.io.IOException;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.lang.Exception;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AvgPassenger_Q2 {

    public static class AverageTripDistanceMapper extends Mapper<Object, Text, Text, DoubleWritable> {

        private final static DoubleWritable one = new DoubleWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            word.set(value);
            String nextLine = word.toString();
            String[] columns = nextLine.split(",");

            Text totalPassengerkey = new Text("Total_Distance");

            Text totalTripKey = new Text("Total_Trip");

            if (!columns[0].trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // first column is converted to date
                    LocalDateTime date = LocalDateTime.parse(columns[1], formatter);
                    System.out.println(date);

                    // map number of passengers to key => Total_Passenger
                    // System.out.println(columns[3]);
                    context.write(totalPassengerkey, new DoubleWritable(Float.parseFloat(columns[4])));

                    // Total trips in the dataset
                    context.write(totalTripKey, one);

                    // unique key for each day passenger
                    DayOfWeek dow = date.getDayOfWeek();

                    // map number of passengers for each day to key => MON_Total_Passenger,
                    // TUE_Total_Passenger
                    Text daysKey = new Text(dow + "_Total_Distance");
                    context.write(daysKey, new DoubleWritable(Float.parseFloat(columns[4])));

                    // Total trips for each day
                    context.write(new Text(dow + "_Total_Trip"), one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get intermediate passenger count for each key

    public static class AverageTripDistanceCombiner extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        private DoubleWritable result = new DoubleWritable();

        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            float count = 0;
            for (DoubleWritable value : values) {
                count += value.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }

        public static class AverageTripDistanceReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();
        float totPassenger = 0;
        
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            if(key.find("Trip")>0){
                for (DoubleWritable val : values){
                    sum+=val.get();
                }
                // ext.write(key, new DoubleWritable(sum));   
            context.write(new Text(key+"_Avg"), new DoubleWritable(totPassenger/sum));

            }

            else {
                
            for (DoubleWritable val : values) {
                sum += val.get();
            }
                totPassenger = sum;
        }
            // result.set(sum);
            // context.write(key, result);
        }
    }

        // remaining
        public static void main(String[] args) throws Exception {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "average trip distance");
            job.setJarByClass(AvgPassenger_Q2.class);
            job.setMapperClass(AverageTripDistanceMapper.class);
            job.setCombinerClass(AverageTripDistanceCombiner.class);
            job.setReducerClass(AverageTripDistanceReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(DoubleWritable.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}
