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

public class Hadoop_Q4B {

    public static class AverageMapper_PerHourAvgPassenger extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

            Text total_Passenger_key = new Text("_Passenger");

            Text total_Trip_Key = new Text("_Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // convert first column to date
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);
                    
                    //identify hour of the day
                    context.write(new Text(date.getHour() + "_Hour" + total_Passenger_key),
                            new IntWritable(Integer.parseInt(col_vals[3])));
                    context.write(new Text(date.getHour() + "_Hour" + total_Trip_Key + "_Avg"), one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get passenger count for each key
    public static class AverageCombiner_PerHourAvgPassenger extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        //reduction to calculate count of each key values
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

    public static class AverageReducer_PerHourAvgPassenger extends Reducer<Text, IntWritable, Text, DoubleWritable> {

        private IntWritable result = new IntWritable();
        double total_Passenger = 0.0;

        //reducing to calulate count each key
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            System.out.println(key);
            int sum = 0;
            //trip key is found and counted
            if (key.find("Trip") > 0) {
                for (IntWritable val : values) {
                    sum += val.get();
                }
                context.write(new Text(key + "_Avg"), new DoubleWritable(total_Passenger / sum));

            }

            else {
                //sum of all values of passenger keys
                for (IntWritable val : values) {
                    sum += val.get();
                }
                total_Passenger = sum;
            }

        }
    }

    public static void main(String[] args) throws Exception {
        //configuration of environment
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average passenger per hour");
        job.setJarByClass(Hadoop_Q4B.class);
        job.setMapperClass(AverageMapper_PerHourAvgPassenger.class);
        job.setCombinerClass(AverageCombiner_PerHourAvgPassenger.class);
        job.setReducerClass(AverageReducer_PerHourAvgPassenger.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
