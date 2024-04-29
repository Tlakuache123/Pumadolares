package com.pumadolares.pumadolares.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.pumadolares.pumadolares.model.JobModel;
import com.pumadolares.pumadolares.repository.JobRepository;

@Component
public class JobInitializer implements CommandLineRunner {
  private final JobRepository jobRepository;

  public JobInitializer(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @Override
  public void run(String... args) {
    if (!jobRepository.existsByName("Sin estableceer")) {
      JobModel job = new JobModel();
      job.setName("Sin establecer");
      jobRepository.save(job);
      System.out.println("Creado el trabajo 'Sin establecer'");
    }
  }
}
