package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.content.Context;
import android.os.AsyncTask;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.network.FareRepresentation;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.utilities.CommonUtils;
import co.gargoyle.supercab.android.utilities.ServerUtils;

import com.google.common.base.Optional;

public class PostFareTask extends AsyncTask<Fare, Integer, Optional<String>> {


  @SuppressWarnings("unused")
  private static final String TAG = "UploadFareTask";

  private PostFareListener mListener;
  private Exception mException;
  private Context mContext;

  public PostFareTask(Context context, PostFareListener listener) {
    mListener = listener;
    mContext = context;
  }

  @Override
  protected Optional<String> doInBackground(Fare... fares) {
    Fare fare = fares[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    ServerUtils.addAuthHeaderToClientResource(mContext, fareProfile);

    try {
      Representation jacksonRep;
      try {
        jacksonRep = ServerUtils.convertFareToJsonRepresentation(fare);
      } catch (IOException e1) {
        e1.printStackTrace();
        mException = e1;
        return Optional.absent();
      }
      Representation rep = fareProfile.post(jacksonRep);
      if (fareProfile.getStatus().isSuccess()) {
        FareRepresentation receivedFare = new FareRepresentation(rep);
        Optional<Fare> optionalFare = receivedFare.getFare();
        if (optionalFare.isPresent()) {
          return Optional.of(optionalFare.get().superCabId);
        } else {
//          Log.d(TAG, "response: " + rep.getText());
          return Optional.absent();
        }
      }
    } catch (ResourceException e) {
      e.printStackTrace();
      mException = e;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<String> fareId) {
    if (mException != null) {
      mListener.handleError(mException);
    }
    mListener.completed(fareId);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI() {
    try {
      String serverUrl = CommonUtils.SERVER_URL + "/fare";
      //String serverUrl = "http://requestb.in/pijt1epi";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}
