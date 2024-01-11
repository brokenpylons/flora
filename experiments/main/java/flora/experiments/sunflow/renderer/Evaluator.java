package flora.experiments.sunflow.renderer;

import flora.Machine;
import flora.Meter;
import flora.WorkUnit;
import flora.experiments.sunflow.RenderingKnobs;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.Filter;
import flora.meter.Stopwatch;
import flora.meter.contrib.EflectMeter;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

final class Evaluator {

    private static final int REFERENCE_TIMEOUT = 60 * 60 * 1000;

    public static void main(String[] args) throws Exception {
      final BufferedImageDisplay display = new BufferedImageDisplay();
      final RenderingKnobs knobs = RenderingKnobs.DEFAULT_KNOBS;
      final RenderingConfiguration dummyConfiguration = RenderingKnobs.DEFAULT_CONFIGURATION;

      final Map<String, Meter> meters =
          Map.of(
              "energy",
              EflectMeter.newLocalMeter(4),
              "runtime",
              new Stopwatch());

      // wire everything together
      final Machine machine =
          new Machine() {
            @Override
            public Map<String, Meter> meters() {
              return new HashMap<>(meters);
            }
          };

      final ConfigurableScene scene = new CornellBox(knobs, dummyConfiguration, display, REFERENCE_TIMEOUT);  // XXX Why do I need to pass a dummy configuration here?
      // XXX: ConfigurableSceneFactory accepts an int[] instead of RenderingConfiguration?

      final var configurations = new RenderingConfiguration[] {
        new RenderingConfiguration(19, 183, 183, -2, -2, 42, 14, Filter.BOX),
        new RenderingConfiguration(20, 205, 205, -1, -1, 24, 19, Filter.BOX),
        new RenderingConfiguration(19, 640, 640, 0, 0, 101, 64, Filter.GAUSSIAN),
        new RenderingConfiguration(14, 147, 147, -2, -2, 40, 5, Filter.BOX),
        new RenderingConfiguration(20, 623, 623, -1, 0, 53, 33, Filter.GAUSSIAN),
        new RenderingConfiguration(15, 142, 142, -2, -2, 40, 2, Filter.BOX),
        new RenderingConfiguration(20, 623, 623, -3, 0, 38, 32, Filter.SINC),
        new RenderingConfiguration(20, 640, 640, 0, 1, 78, 64, Filter.BLACKMAN_HARRIS),
        new RenderingConfiguration(19, 640, 640, -1, 1, 43, 28, Filter.BLACKMAN_HARRIS),
        new RenderingConfiguration(20, 522, 522, -2, 0, 39, 28, Filter.LANCZOS),
        new RenderingConfiguration(19, 634, 634, -2, 1, 54, 29, Filter.GAUSSIAN),
      };

      for (final var configuration : configurations) {
        final WorkUnit<?, ?> work = scene.newScene(scene.knobs(), configuration);
        final Map<String, Double> measurement = machine.run(work);
        final double[] measures = measurement.values().stream().mapToDouble(d -> d).toArray();
        System.out.println(Arrays.toString(measures));
      }
    }
}
