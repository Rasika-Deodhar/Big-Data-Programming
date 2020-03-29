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

public class Hadoop_Q5A {

    public static class AverageMapper_TripDistancePerWeekDay extends Mapper<Object, Text, Text, DoubleWritable> {

        private final static DoubleWritable one = new DoubleWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //reading each word and line with delimiter ,
            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

           
            //Passenger and Trip keys instantiated
            Text total_Passenger_key = new Text("Passenger");
            Text total_Trip_Key = new Text("Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    //converted first column to date
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);

                    DayOfWeek days_of_week = date.getDayOfWeek();

                    //for weekends -> write accordingly
                    if (days_of_week.toString() == "SATURDAY" || days_of_week.toString() == "SUNDAY") {

                        context.write(new Text(date.getHour() + "_Weekend_" + total_Passenger_key),
                                new DoubleWritable(Float.parseFloat(col_vals[4])));
                        context.write(new Text(date.getHour() + "_Weekend_" + total_Trip_Key), one);
                    }

                    //similarily for weekdays
                    else {

                        context.write(new Text(date.getHour() + "_Weekday_" + total_Passenger_key),
                                new DoubleWritable(Float.parseFloat(col_vals[4])));
                        context.write(new Text(date.getHour() + "_Weekday_" + total_Trip_Key), one);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get passenger count for each key
    public static class AverageCombiner_TripDistancePerWeekDay extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        private DoubleWritable result = new DoubleWritable();

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

    public static class AverageReducer_TripDistancePerWeekDay extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();
        float totPassenger = 0;

        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {

            //get count of keys for Trip values
            System.out.println(key);
            int sum = 0;
            if (key.find("Trip") > 0) {
                for (DoubleWritable val : values) {
                    sum += val.get();
                }
                // ext.write(key, new DoubleWritable(sum));
                context.write(new Text(key + "Distance_Avg"), new DoubleWritable(totPassenger / sum));

            }
            //get total number of passengers by taking sum
            else {

                for (DoubleWritable val : values) {
                    sum += val.get();
                }
                totPassenger = sum;
            }

        }
    }

    public static void main(String[] args) throws Exception {
        //configuring environment to run all functions
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "trip distance per weekday and weekend");
        job.setJarByClass(Hadoop_Q5A.class);
        job.setMapperClass(AverageMapper_TripDistancePerWeekDay.class);
        job.setCombinerClass(AverageCombiner_TripDistancePerWeekDay.class);
        job.setReducerClass(AverageReducer_TripDistancePerWeekDay.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
