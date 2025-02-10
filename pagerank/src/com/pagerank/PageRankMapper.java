package com.pagerank;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class PageRankMapper extends Mapper<Object, Text, Text, Text> {
    private static final double DAMPING_FACTOR = 0.85;
    private static final double INITIAL_PAGERANK = 1.0;

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Split the line by tab to separate node and its outlinks
        String[] parts = value.toString().split("\t");

        // Check if the input line is valid (i.e., it has a node and its outlinks)
        if (parts.length == 2) {
            String node = parts[0];
            String[] outlinks = parts[1].split(" "); // Split outlinks by space

            // Emit the outlink as a contribution to other nodes
            double contribution = INITIAL_PAGERANK / outlinks.length;
            for (String outlink : outlinks) {
                context.write(new Text(outlink), new Text("CONTRIBUTION:" + contribution));
            }

            // Emit the current node with its initial PageRank
            context.write(new Text(node), new Text("PAGERANK:" + INITIAL_PAGERANK));
        } else {
            // Handle malformed input
            System.err.println("Skipping malformed line: " + value.toString());
        }
    }
}

