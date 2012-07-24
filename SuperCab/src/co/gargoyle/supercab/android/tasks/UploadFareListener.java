package co.gargoyle.supercab.android.tasks;

import com.google.common.base.Optional;

public interface UploadFareListener {

  public void completed(Optional<Long> fareId);

  public void handleError(Throwable exception);

}