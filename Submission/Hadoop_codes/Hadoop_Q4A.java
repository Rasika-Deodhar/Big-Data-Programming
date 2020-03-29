import java.io.IOException;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.lang.Exception;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Hadoop_Q4A {

    public static class AverageMapper_PerWeekdayAvgPassenger extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //reading words from csv one by one
            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

            Text total_Passenger_key = new Text("_Passenger");
            Text total_Trip_key = new Text("_Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    
                    // changing the type of first column to date value
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);

                    // getting week days
                    DayOfWeek days_of_week = date.getDayOfWeek();

                    // For weekend - mention weekend
                    if (days_of_week.toString() == "SATURDAY" || days_of_week.toString() == "SUNDAY") {

                        context.write(new Text(date.getHour() + "Weekend_" + total_Passenger_key),
                                new IntWritable(Integer.parseInt(col_vals[3])));
                        context.write(new Text(date.getHour() + "Weekend_" + total_Trip_key), one);
                    }
                    // For weekday - mention weekday
                    else {

                        context.write(new Text(date.getHour() + "Weekday_" + total_Passenger_key),
                                new IntWritable(Integer.parseInt(col_vals[3])));
                        context.write(new Text(date.getHour() + "Weekday_" + total_Trip_key), one);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get passenger count for each key
    public static class AverageCombiner_PerWeekdayAvgPassenger extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        //counting the independent values
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }

    public static class AverageReducer_PerWeekdayAvgPassenger extends Reducer<Text, IntWritable, Text, DoubleWritable> {

        private IntWritable result = new IntWritable();
        double totPassenger = 0.0;

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            System.out.println(key);
            int sum = 0;
            if (key.find("Trip") > 0) {
                for (IntWritable val : values) {
                    sum += val.get();
                }
                // ext.write(key, new DoubleWritable(sum));
                context.write(new Text(key + "_Avg"), new DoubleWritable(totPassenger / sum));

            }

            else {

                for (IntWritable val : values) {
                    sum += val.get();
                }
                totPassenger = sum;
            }

        }
    }

    // remaining
    public static void main(String[] args) throws Exception {

        //Environment Configuration
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average passenger for weekend and weekday");
        job.setJarByClass(Hadoop_Q4A.class);
        job.setMapperClass(AverageMapper_PerWeekdayAvgPassenger.class);
        job.setCombinerClass(AverageCombiner_PerWeekdayAvgPassenger.class);
        job.setReducerClass(AverageReducer_PerWeekdayAvgPassenger.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
