package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.utilities.CommonUtilities;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;
import co.gargoyle.supercab.android.utilities.ServerUtilities;

import com.google.common.base.Optional;

public class PostFareTask extends AsyncTask<Fare, Integer, Optional<Long>> {

   private PostFareListener mListener;
   private Exception mException;
   private Context mContext;

   public PostFareTask(Context context, PostFareListener listener) {
     mListener = listener;
     mContext = context;
   }

  private static final String TAG = "UploadFareTask";

  @Override
  protected Optional<Long> doInBackground(Fare... fares) {
    Fare fare = fares[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    Optional<UserCredentials> credentials = new PreferenceUtils(mContext).getCredentials();
    if (credentials.isPresent()) {
      fareProfile.setChallengeResponse(ChallengeScheme.HTTP_BASIC, 
                                       credentials.get().username,
                                       credentials.get().password);
    }

    try {
      Representation jacksonRep;
      try {
        jacksonRep = ServerUtilities.convertFareToJsonRepresentation(fare);
      } catch (IOException e1) {
        e1.printStackTrace();
        mException = e1;
        return Optional.absent();
      }
      //Representation rep = fareProfile.post();
      Representation rep = fareProfile.post(jacksonRep);
      if (fareProfile.getStatus().isSuccess()) {
        try {
          Log.d(TAG, "response: " + rep.getText());
          return Optional.of(0L);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (ResourceException e) {
      e.printStackTrace();
      mException = e;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<Long> fareId) {
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
      //String serverUrl = CommonUtilities.SERVER_URL + "/fare/new";
      String serverUrl = CommonUtilities.SERVER_URL + "/api/v1/fare/";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}