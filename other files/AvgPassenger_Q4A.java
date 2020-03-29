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

public class AvgPassenger_Q4A {

    public static class AveragePassengerPerHourMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            word.set(value);
            String nextLine = word.toString();
            String[] columns = nextLine.split(",");

            // Weekday_Total__Passenger_0
            // Weekday_Total_Trip_0

            Text totalPassengerkey = new Text("_Passenger");

            Text totalTripKey = new Text("_Trip");

            if (!columns[0].trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // first column is converted to date
                    LocalDateTime date = LocalDateTime.parse(columns[1], formatter);

                    DayOfWeek dow = date.getDayOfWeek();

                    if (dow.toString() == "SATURDAY" || dow.toString() == "SUNDAY") {

                        context.write(new Text(date.getHour() + "Weekend_" + totalPassengerkey),
                                new IntWritable(Integer.parseInt(columns[3])));
                        context.write(new Text(date.getHour() + "Weekend_" + totalTripKey), one);
                    }

                    else {

                        context.write(new Text(date.getHour() + "Weekday_" + totalPassengerkey),
                                new IntWritable(Integer.parseInt(columns[3])));
                        context.write(new Text(date.getHour() + "Weekday_" + totalTripKey), one);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get intermediate passenger count for each key
    public static class AveragePassengerPerHourCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

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

    public static class AveragePassengerPerHourReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

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
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average passenger per hour");
        job.setJarByClass(AvgPassenger_Q4A.class);
        job.setMapperClass(AveragePassengerPerHourMapper.class);
        job.setCombinerClass(AveragePassengerPerHourCombiner.class);
        job.setReducerClass(AveragePassengerPerHourReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
