/*
 * SonarSource :: .NET :: Shared library
 * Copyright (C) 2014-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.dotnet.shared.plugins;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;
import org.sonarsource.dotnet.shared.plugins.protobuf.FileMetadataImporter;
import org.sonarsource.dotnet.shared.plugins.protobuf.ProtobufImporters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeneratedFileFilterTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Rule
  public LogTester logs = new LogTester();

  private Set<Path> generatedPaths;

  private AbstractConfiguration configuration;
  private ProtobufImporters protobufImporterFactory;
  private FileMetadataImporter fileMetadataImporter;

  @Test
  public void accept_returns_false_for_autogenerated_files() throws IOException {
    // Arrange
    GeneratedFileFilter filter = createFilter();
    generatedPaths.add(Paths.get("c:\\autogenerated"));

    // Act
    Boolean result = filter.accept(newInputFile("c:/autogenerated"));

    // Assert
    assertThat(result).isFalse();
    assertThat(logs.logs(LoggerLevel.DEBUG)).contains("Skipping auto generated file: c:/autogenerated");
  }

  @Test
  public void accept_returns_true_for_nonautogenerated_files() throws IOException {
    // Arrange
    GeneratedFileFilter filter = createFilter();
    generatedPaths.add(Paths.get("c:\\autogenerated"));

    // Act
    Boolean result = filter.accept(newInputFile("File1"));

    // Assert
    assertThat(result).isTrue();
    assertThat(logs.logs(LoggerLevel.DEBUG)).isEmpty();
  }

  @Test
  public void is_initialized_only_once() throws IOException {
    // Arrange
    GeneratedFileFilter filter = createFilter();

    // Act - call accept several times and ensure we initialize the filter only once
    filter.accept(newInputFile("File1"));
    filter.accept(newInputFile("File2"));
    filter.accept(newInputFile("File3"));

    // Assert
    verify(configuration, times(1)).protobufReportPath();
    verify(protobufImporterFactory, times(1)).fileMetadataImporter();
    verify(fileMetadataImporter, times(1)).accept(anyObject());
  }

  private InputFile newInputFile(String path) {
    InputFile file = mock(InputFile.class);
    when(file.path()).thenReturn(Paths.get(path));
    when(file.toString()).thenReturn(path);
    return file;
  }

  private GeneratedFileFilter createFilter() throws IOException {
    generatedPaths = new HashSet<>();

    fileMetadataImporter = mock(FileMetadataImporter.class);
    when(fileMetadataImporter.getGeneratedFilePaths()).thenReturn(generatedPaths);

    protobufImporterFactory = mock(ProtobufImporters.class);
    when(protobufImporterFactory.fileMetadataImporter()).thenReturn(fileMetadataImporter);

    configuration = mock(AbstractConfiguration.class);

    // Create temporary empty protobuf to satisfy a file.exists check, the content is ignored
    temp.newFile("file-metadata.pb");

    when(configuration.protobufReportPath()).thenReturn(Optional.of(temp.getRoot().toPath()));

    return new GeneratedFileFilter(configuration, protobufImporterFactory);
  }
}
