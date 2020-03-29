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

public class Hadoop_Q5B {

    public static class AverageMapper_TripDistancePerHour extends Mapper<Object, Text, Text, DoubleWritable> {

        private final static DoubleWritable one = new DoubleWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //reading from word to word and new line
            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

            Text total_Passenger_key = new Text("Passenger");
            Text total_Trip_Key = new Text("Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // first column is converted to date
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);
                    
                    //write key for each hour
                    context.write(new Text(date.getHour() + "_Hour" + total_Passenger_key),
                            new DoubleWritable(Float.parseFloat(col_vals[4])));

                    context.write(new Text(date.getHour() + "_Hour" + total_Trip_Key), one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get passenger count for each key
    public static class AverageCombiner_TripDistancePerHour extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        private DoubleWritable result = new DoubleWritable();

        //reduce for finding sum of each key
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            int count = 0;
            for (DoubleWritable value : values) {
                count += value.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }

    public static class AverageReducer_TripDistancePerHour extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();
        float total_Passengers = 0;

        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {

            System.out.println(key);
            int sum = 0;
            //for each trip key, find the sum of values
            if (key.find("Trip") > 0) {
                for (DoubleWritable val : values) {
                    sum += val.get();
                }
                // ext.write(key, new DoubleWritable(sum));
                context.write(new Text(key + "Distance_Avg"), new DoubleWritable(total_Passengers / sum));

            }

            else {
                //for each passenger calculater total number of passengers
                for (DoubleWritable val : values) {
                    sum += val.get();
                }
                total_Passengers = sum;
            }

        }
    }

    public static void main(String[] args) throws Exception {
        //Configuring environment to run code
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "trip distance per hour");
        job.setJarByClass(Hadoop_Q5B.class);
        job.setMapperClass(AverageMapper_TripDistancePerHour.class);
        job.setCombinerClass(AverageCombiner_TripDistancePerHour.class);
        job.setReducerClass(AverageReducer_TripDistancePerHour.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
