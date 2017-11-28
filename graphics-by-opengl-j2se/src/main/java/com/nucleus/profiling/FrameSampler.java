package com.nucleus.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nucleus.SimpleLogger;

/**
 * Utility class for keeping track of delta times, normally used to calculate the delta time from one frame to the next.
 * Singleton class that can be
 * 
 * @author Richard Sahlin
 *
 */
public class FrameSampler {

    public interface SampleInfo {
        /**
         * Returns the tag for the sample
         * 
         * @return
         */
        public String getTag();

        /**
         * Returns the sample log detail level, used to filter sample logs.
         * 
         * @return
         */
        public Level getDetail();
    }

    public static class Sample {
        public int total;
        public int max;
        public int min;
        public int count;

        /**
         * Creates an empty sample
         */
        public Sample() {
        }

        /**
         * Creates a sample with one value
         * 
         * @param millis
         */
        public Sample(int millis) {
            total = millis;
            max = millis;
            min = millis;
            count = 1;
        }

        public void add(int millis) {
            total += millis;
            if (max < millis) {
                max = millis;
            }
            if (min > millis) {
                min = millis;
            }
            count++;
        }

        public int getCount() {
            return count;
        }

        public int getAverage() {
            return total / count;
        }

        public void reset() {
            total = 0;
            max = 0;
            min = Integer.MAX_VALUE;
            count = 0;
        }

        @Override
        public String toString() {
            return "Average: " + getAverage() + " Max: " + max + " Min: " + min;
        }
    }

    public enum Samples implements SampleInfo {

        DISPLAY_SPLASH(Level.NORMAL),
        SET_ROOT_NODE(Level.NORMAL),
        LOAD_SCENE(Level.NORMAL),
        CREATE_SCENE(Level.NORMAL),
        CREATE_NODE(Level.NORMAL),
        LOAD_MAP(Level.NORMAL),
        CREATE_SHADER(Level.NORMAL),
        COMPONENTPROCESSOR(Level.NORMAL),
        PROCESSCOMPONENT(Level.HIGH),
        RENDERNODES(Level.NORMAL),
        CREATE_TEXTURE(Level.NORMAL),
        CREATE_IMAGE(Level.NORMAL),
        LOAD_IMAGE(Level.NORMAL),
        COPY_IMAGE(Level.NORMAL),
        GENERATE_MIPMAPS(Level.NORMAL),
        DRAWFRAME(Level.NORMAL);

        public final Level detail;

        private Samples(Level detail) {
            this.detail = detail;
        }

        @Override
        public String getTag() {
            return name();
        }

        @Override
        public Level getDetail() {
            return detail;
        }

    }

    public final static int DEFAULT_MIN_FPS = 30;
    private static FrameSampler frameSampler = new FrameSampler();

    private final static int DEFAULT_AVERAGE_VALUES = 300;

    /**
     * Start time of sampler
     */
    private final long samplerStart = System.currentTimeMillis();
    private long previousTime;
    private long currentTime;
    private int minFPS = DEFAULT_MIN_FPS;
    private float delta = (float) 1 / DEFAULT_MIN_FPS;
    private float maxDelta;

    private float totalDelta;
    private int frames;
    private long vertices;
    private long indices;
    private int drawCalls;
    private long sampleStart;

    private Map<String, Sample> tagTimings = new HashMap<>();
    private Map<String, ArrayList<Long>> tagStartTimes = new HashMap<>();

    public enum Level {
        LOW(1),
        NORMAL(2),
        HIGH(3);

        public final int value;

        private Level(int value) {
            this.value = value;
        }
    }

    /**
     * Adjust to log different sample timings, read/write this in your code
     */
    public Level sampleDetail = Level.NORMAL;

    /**
     * Returns the sampler instance
     * 
     * @return
     */
    public static FrameSampler getInstance() {
        return frameSampler;
    }

    /**
     * Updates to the current time, returning the delta time in seconds from previous frame this value will be corrected
     * by the min fps value.
     * 
     * @return Delta time in seconds from previous frame, this value will be checked for minimum fps. If min fps is 10
     * then this value will not be greater than 1/10 second.
     * The max delta value will be returned the first time this method is called (ie the first frame)
     */
    public float update() {
        previousTime = currentTime;
        currentTime = System.currentTimeMillis();
        delta = (float) (currentTime - previousTime) / 1000;
        frames++;
        totalDelta += delta;
        if (delta > (1f / minFPS)) {
            delta = 1f / minFPS;
        }
        return delta;
    }

    /**
     * Sets the min fps value
     * 
     * @param minFps
     */
    public void setMinFps(int minFps) {
        minFPS = minFps;

    }

    /**
     * Adds the number of vertices sent to drawArrays
     * 
     * @param vertices Number of vertices
     */
    public void addDrawArrays(int vertices) {
        this.vertices += vertices;
        drawCalls++;
    }

    public void addDrawElements(int vertices, int indices) {
        this.vertices += vertices;
        this.indices += indices;
        drawCalls++;
    }

    /**
     * Returns the current delta value, time in seconds from previous frame.
     * If a call to {@link #setMinFPS(int)} has been made then the delta value is limited according to this.
     * Will be 1 / DEFAULT_MIN_FPS before the first frame has finished.
     * 
     * @return The delta value for previous -> current frame, will be limited if {@link #setMinFPS(int)} has been
     * called.
     */
    public float getDelta() {
        if (maxDelta > 0) {
            if (delta > maxDelta) {
                return maxDelta;
            }
        }
        return delta;
    }

    /**
     * Returns the average fps, resetting the average values and setting sample start to the current time.
     * 
     * @return Average FPS info - same as calling toString() then resetting the values with clear()
     */
    public String sampleFPS() {
        String info = toString();
        clear();
        sampleStart = currentTime;
        return info;
    }

    /**
     * Returns the seconds from last call to sampleFPS()
     * 
     * @return
     */
    public float getSampleDuration() {
        if (sampleStart == 0) {
            sampleStart = System.currentTimeMillis();
            return 0;
        }
        return (int) (currentTime - sampleStart) / 1000;
    }

    /**
     * Clears all sample values
     */
    public void clear() {
        totalDelta = 0;
        frames = 0;
        indices = 0;
        vertices = 0;
        drawCalls = 0;
    }

    /**
     * Sets the min fps value, a value of 20 means that the delta-time, as returned by getDelta(), will never go above
     * 50 milliseconds (1/20 s).
     * Use this to limit the lowest fps for animations/logic - note that slowdown will occur of the client platform
     * cannot provide a fps that is higher.
     * 
     * @param fps Min fps, the value of getDelta() will be larger than (1/fps)
     */
    public void setMinFPS(int fps) {
        maxDelta = (float) 1 / fps;
    }

    @Override
    public String toString() {
        int fps = (int) (frames / totalDelta);
        return "Average FPS: " + fps + ", " + vertices / frames + " vertices, " + indices / frames
                + " indices, " + drawCalls / frames + " drawcall - per frame";
    }

    /**
     * Returns number of millis since the sampler was started, until now
     * 
     * @return
     */
    public long getMillisFromStart() {
        return getMillisFromStart(System.currentTimeMillis());
    }

    /**
     * Returns the number of millis since the sampler was started and now
     * 
     * @param now
     * @return Number of millis between start of sampler and now
     */
    public long getMillisFromStart(long now) {
        return now - samplerStart;
    }

    /**
     * Outputs the time between start of sampler and now, use this to measure startup time and similar
     * 
     * @param info Identifier for sample
     */
    public void logTag(SampleInfo info) {
        logTag(info, samplerStart, System.currentTimeMillis());
    }

    /**
     * Outputs the time between start of sampler and endtime
     * 
     * @param info Identifier for sample
     * @param endTime Time when sample ended
     */
    public void logTag(SampleInfo info, long endTime) {
        logTag(info, samplerStart, endTime);
    }

    /**
     * Outputs the time between startTime and endtime
     * 
     * @param info Identifier for sample
     * @param startTime Time when sample started
     * @param endTime Time when sample ended
     */
    public void logTag(SampleInfo info, long startTime, long endTime) {
        if (info.getDetail().value >= sampleDetail.value) {
            SimpleLogger.d(getClass(), "Sample " + info.getTag() + " : " + (endTime - startTime) + " millis.");
        }
    }

    /**
     * Outputs the time between startTime and endtime
     * 
     * @param info Identifier for sample
     * @param extra Extra identifier tat
     * @param startTime Time when sample started
     * @param endTime Time when sample ended
     */
    public void logTag(SampleInfo info, String extra, long startTime, long endTime) {
        if (info.getDetail().value >= sampleDetail.value) {
            SimpleLogger.d(getClass(), "Sample " + info.getTag() + extra + " : " + (endTime - startTime) + " millis.");
        }
    }

    /**
     * Adds the tag timing, outputs min/max/average at specified interval
     * 
     * @param tag
     * @param startTime
     * @param endTime The end time of interval
     * @param detail The sample log level, if current level is equal or higher then this sample is added. Otherwise it
     * is skipped.
     */
    public void addTag(String tag, long startTime, long endTime, Level detail) {
        if (sampleDetail.value >= detail.value) {
            Sample sample = tagTimings.get(tag);
            int millis = (int) (endTime - startTime);
            if (sample == null) {
                sample = new Sample(millis);
                tagTimings.put(tag, sample);
            } else {
                sample.add(millis);
                if (sample.getCount() >= DEFAULT_AVERAGE_VALUES) {
                    logAverage(tag, sample);
                    sample.reset();
                }
            }
        }
    }

    /**
     * Adds the tag timing, outputs min/max/average at specified interval
     * 
     * @param info
     * @param startTime
     * @param endTime The end time of interval
     */
    public void addTag(SampleInfo info, long startTime, long endTime) {
        addTag(info.getTag(), startTime, endTime, info.getDetail());
    }

    /**
     * Adds start time to a tag, must be finalized with a call to {@link #setEndTimes(String, long)}
     * 
     * @param tag
     * @param startTime
     * @param detail The sample log level, if current level is equal or higher then this sample is added. Otherwise it
     * is skipped.
     */
    public void addTag(String tag, long startTime, Level detail) {
        if (sampleDetail.value >= detail.value) {
            synchronized (tagStartTimes) {
                ArrayList<Long> start = tagStartTimes.get(tag);
                if (start == null) {
                    start = new ArrayList<>();
                    tagStartTimes.put(tag, start);
                }
                start.add(startTime);
            }
        }
    }

    /**
     * Adds start time to a tag, must be finalized with a call to {@link #setEndTimes(SampleInfo, long)}
     * 
     * @param info
     * @param startTime
     */
    public void addTag(SampleInfo info, long startTime) {
        addTag(info.getTag(), startTime, info.getDetail());
    }

    /**
     * Sets the end times that have {@value #UNDEFINED}
     * 
     * @param tag
     * @param endTime
     * @param detail The sample log level, if current level is equal or higher then this sample is finalized.
     */
    public void setEndTimes(String tag, long endTime, Level detail) {
        synchronized (tagStartTimes) {
            ArrayList<Long> start = tagStartTimes.get(tag);
            if (start != null) {
                for (long s : start) {
                    addTag(tag, s, endTime, detail);
                }
            }
            tagStartTimes.clear();
        }
    }

    /**
     * Sets the end times that have {@value #UNDEFINED}
     * 
     * @param info
     * @param endTime
     */
    public void setEndTimes(SampleInfo info, long endTime) {
        setEndTimes(info.getTag(), endTime, info.getDetail());
    }

    /**
     * Returns the sample for the specified tag, or null if not found.
     * This is a reference to the Sample used to track values - changes will be reflected in the object stored
     * with the tag.
     * 
     * @param tag
     * @return Reference to the Sample, or null if no Sample for the tag
     */
    public Sample getSample(String tag) {
        return tagTimings.get(tag);
    }

    /**
     * Sets a sample for the tag
     * 
     * @param tag
     * @param sample Sample to set or null to clear.
     */
    public void setSample(String tag, Sample sample) {
        tagTimings.put(tag, sample);
    }

    private void logAverage(String tag, Sample sample) {
        SimpleLogger.d(getClass(), "Sampler tag " + tag + " : " + sample.toString());
    }

}
