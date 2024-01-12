package eflect.validation;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Iterator;

import java.io.IOException;

import java.util.Optional;

class Reader {
    private final String file;

    public Reader(String file) {
        this.file = file;
    }

    public String read() throws IOException {
        return Files.readString(Paths.get(this.file));
    }

    void benchmark() throws IOException {
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            read();
        }
        final long end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
    }
}

class PowercapReader {
    private final Reader reader;

    public PowercapReader(Reader reader) {
        this.reader = reader;
    }

    public double read() throws IOException {
        return Double.parseDouble(reader.read()) / 1.0e6;
    }
}

record PowercapSample(long time, double reading, double value) {};

class PowercapCollector {
    final PowercapReader reader;
    double previous = Double.NaN;
    String name;

    public PowercapCollector(String name, PowercapReader reader) {
        this.name = name;
        this.reader = reader;
    }

    public Optional<PowercapSample> tryNext() {
        try {
            double reading = reader.read();
            if (previous > reading) {
                throw new RuntimeException("Accumulator overflow");
            }
            if (previous != reading) {
                final var value = reading - previous;
                previous = reading;
                return Optional.of(new PowercapSample(System.currentTimeMillis(), reading, value));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}

final public class Trace {


  public static void main(String[] args) throws Exception {
      final var r0 = new Reader("/sys/devices/virtual/powercap/intel-rapl/intel-rapl:0/energy_uj");
      final var r00 = new Reader("/sys/devices/virtual/powercap/intel-rapl/intel-rapl:0/intel-rapl:0:0/energy_uj");
      final var r01 = new Reader("/sys/devices/virtual/powercap/intel-rapl/intel-rapl:0/intel-rapl:0:1/energy_uj");
      final var c0 = new PowercapCollector("0", new PowercapReader(r0));
      final var c00 = new PowercapCollector("00", new PowercapReader(r00));
      final var c01 = new PowercapCollector("01", new PowercapReader(r01));

      while (true) {
          final var s0 = c0.tryNext();
          final var s00 = c00.tryNext(); // For some reason this one updates much faster than the others???
          final var s01 = c01.tryNext();
          if (s0.isPresent() && s00.isPresent() && s01.isPresent()) {
              final var s0_ = s0.get();
              final var s00_ = s00.get();
              final var s01_ = s01.get();
              if (s0_.time() == s00_.time() && s0_.time() == s01_.time()) {
                  System.out.println(String.format("%d %.8f %.8f %.8f", s0_.time(), s0_.value(), s00_.value(), s01_.value()));
              } else {
                  System.out.println("MISALIGNED");
              }
          }
      }
  }
}


