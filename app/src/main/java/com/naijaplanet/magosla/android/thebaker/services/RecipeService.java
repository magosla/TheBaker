package com.naijaplanet.magosla.android.thebaker.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.data.pojo.Ingredient;
import com.naijaplanet.magosla.android.thebaker.data.pojo.Recipe;
import com.naijaplanet.magosla.android.thebaker.data.pojo.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.data.pojo.Recipes;
import com.naijaplanet.magosla.android.thebaker.data.provider.IngredientsColumn;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeProvider;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeStepsColumn;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipesColumn;
import com.naijaplanet.magosla.android.thebaker.receivers.RecipeServiceReceiver;
import com.naijaplanet.magosla.android.thebaker.utils.GsonRequest;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class RecipeService extends IntentService {
    private static final String PACKAGE_NAME = RecipeService.class.getPackage().getName();
    private static final String ACTION_DOWNLOAD_RECIPE = PACKAGE_NAME + ".action.download_recipe";
    private static final String TAG = RecipeService.class.getName();
    private ResultReceiver mResultReceiver;

    public RecipeService() {
        super(RecipeService.class.getSimpleName());
    }

    public RecipeService(String name) {
        super(name);
    }

    /**
     * Starts a service to update the recipe database in the background
     *
     * @param context the application
     */
    public static void updateRecipeDatabase(@NonNull Context context, RecipeServiceReceiver.Callback receiverCallback) {
        RecipeServiceReceiver recipeServiceReceiver = new RecipeServiceReceiver(new Handler(context.getMainLooper()));

        recipeServiceReceiver.setCallback(receiverCallback);

        Intent intent = new Intent(context, RecipeService.class);
        intent.setAction(ACTION_DOWNLOAD_RECIPE);
        intent.putExtra(PARAM.RECEIVER_CALLBACK.name(), recipeServiceReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if (ACTION_DOWNLOAD_RECIPE.equals(action)) {
            // the is an intent to download recipe online
            mResultReceiver = intent.getParcelableExtra(PARAM.RECEIVER_CALLBACK.name());

            // Notify the Receiver that the process is about to start
            mResultReceiver.send(RecipeServiceReceiver.RESULT_INIT, null);

            fetchAndLoad();
        }
    }

    private void fetchAndLoad() {
        Log.i("logger", "loading now");
        @SuppressWarnings("Convert2MethodRef") GsonRequest<Recipes> gsonRequest =
                new GsonRequest<>(Config.RECIPE_URL, Recipes.class, null, response ->
                    insertData(response)
                , error -> {
                    Bundle bundle = new Bundle();

                    RecipeServiceReceiver.Response response;

                    NetworkResponse networkResponse =error.networkResponse;

                    if(networkResponse == null || networkResponse.statusCode < 200){
                        response = RecipeServiceReceiver.Response.NO_INTERNET;
                    }else{
                        response = RecipeServiceReceiver.Response.UNKNOWN_ERROR;
                    }

                    bundle.putSerializable(RecipeServiceReceiver.BUNDLE_RESPONSE
                            , response);

                    mResultReceiver.send(RecipeServiceReceiver.RESULT_ERROR, bundle);

                });
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(gsonRequest);

    }

    private void insertData(Recipes recipes) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        //  ArrayList<ContentProviderOperation> stepsBatchOperations = new ArrayList<>();
        //  ArrayList<ContentProviderOperation> ingBatchOperations = new ArrayList<>();

        int stepNo;
        for (Recipe recipe : recipes) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    RecipeProvider.Recipes.CONTENT_URI);

            builder.withValue(RecipesColumn.ID, recipe.getId());
            builder.withValue(RecipesColumn.NAME, recipe.getName());
            builder.withValue(RecipesColumn.SERVINGS, recipe.getServings());

            batchOperations.add(builder.build());

            stepNo = 0;
            for (RecipeStep recipeStep : recipe.getSteps()) {
                ContentProviderOperation.Builder stepBuilder = ContentProviderOperation.newInsert(
                        RecipeProvider.RecipeSteps.CONTENT_URI);

                stepBuilder.withValue(RecipeStepsColumn.RECIPE_ID, recipe.getId());
                //stepBuilder.withValue(RecipeStepsColumn.STEP_NO, recipeStep.getId());
                stepBuilder.withValue(RecipeStepsColumn.STEP_NO, stepNo++);
                stepBuilder.withValue(RecipeStepsColumn.SHORT_DESCRIPTION, recipeStep.getShortDescription());
                stepBuilder.withValue(RecipeStepsColumn.DESCRIPTION, recipeStep.getDescription());
                stepBuilder.withValue(RecipeStepsColumn.VIDEO_URL, recipeStep.getVideoURL());
                stepBuilder.withValue(RecipeStepsColumn.THUMBNAIL, recipeStep.getThumbnailURL());
                batchOperations.add(stepBuilder.build());
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
                ContentProviderOperation.Builder ingredientBuilder = ContentProviderOperation.newInsert(
                        RecipeProvider.Ingredients.CONTENT_URI);
                ingredientBuilder.withValue(IngredientsColumn.RECIPE_ID, recipe.getId());
                ingredientBuilder.withValue(IngredientsColumn.QUANTITY, ingredient.getQuantity());
                ingredientBuilder.withValue(IngredientsColumn.MEASURE, ingredient.getMeasure());
                ingredientBuilder.withValue(IngredientsColumn.INGREDIENT, ingredient.getIngredient());

                batchOperations.add(ingredientBuilder.build());

            }
        }

        Bundle bundle = new Bundle();

        try {
            getContentResolver().applyBatch(RecipeProvider.AUTHORITY, batchOperations);
            //  getContentResolver().applyBatch(RecipeProvider.AUTHORITY, stepsBatchOperations);
            //  getContentResolver().applyBatch(RecipeProvider.AUTHORITY, ingBatchOperations);

            Log.i(TAG, "loading now - Data loaded");

            // Notify the Widgets for Updates
            Config.notifyWidgetUpdate(this);

            bundle.putSerializable(RecipeServiceReceiver.BUNDLE_RESPONSE, RecipeServiceReceiver.Response.SUCCESS);
            mResultReceiver.send(RecipeServiceReceiver.RESULT_OK, bundle);

        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "loading now - Error applying batch insert", e);
            bundle.putSerializable(RecipeServiceReceiver.BUNDLE_RESPONSE, RecipeServiceReceiver.Response.UNKNOWN_ERROR);
            mResultReceiver.send(RecipeServiceReceiver.RESULT_ERROR, bundle);
        }
    }

    private enum PARAM {
        RECEIVER_CALLBACK
    }
}
