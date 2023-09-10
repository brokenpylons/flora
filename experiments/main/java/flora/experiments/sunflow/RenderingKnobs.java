package flora.experiments.sunflow;

import flora.knob.EnumKnob;
import flora.knob.IntRangeKnob;
import java.util.concurrent.ThreadLocalRandom;

public record RenderingKnobs(
    IntRangeKnob threads,
    IntRangeKnob resolutionX,
    IntRangeKnob resolutionY,
    IntRangeKnob aaMin,
    IntRangeKnob aaMax,
    IntRangeKnob bucketSize,
    IntRangeKnob aoSamples,
    EnumKnob<Filter> filter) {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  /** The default knobs used for rendering. */
  public static RenderingKnobs defaultKnobs() {
    return new RenderingKnobs(
        /* threads= */ new IntRangeKnob(1, CPU_COUNT),
        /* resolutionX= */ new IntRangeKnob(256, 768),
        /* resolutionY= */ new IntRangeKnob(256, 768),
        /* aaMin= */ new IntRangeKnob(-4, 5),
        /* aaMax= */ new IntRangeKnob(-4, 5),
        /* bucketSize= */ new IntRangeKnob(1, 128),
        /* aoSamples= */ new IntRangeKnob(32, 128),
        new EnumKnob<>(Filter.class));
  }

  public RenderingConfiguration randomConfiguration() {
    return new RenderingConfiguration(
        threads().fromIndex(ThreadLocalRandom.current().nextInt(threads().configurationCount())),
        resolutionX()
            .fromIndex(ThreadLocalRandom.current().nextInt(resolutionX().configurationCount())),
        resolutionY()
            .fromIndex(ThreadLocalRandom.current().nextInt(resolutionY().configurationCount())),
        aaMin().fromIndex(ThreadLocalRandom.current().nextInt(aaMin().configurationCount())),
        aaMax().fromIndex(ThreadLocalRandom.current().nextInt(aaMax().configurationCount())),
        aoSamples()
            .fromIndex(ThreadLocalRandom.current().nextInt(aoSamples().configurationCount())),
        bucketSize()
            .fromIndex(ThreadLocalRandom.current().nextInt(bucketSize().configurationCount())),
        filter().fromIndex(ThreadLocalRandom.current().nextInt(filter().configurationCount())));
  }
}
