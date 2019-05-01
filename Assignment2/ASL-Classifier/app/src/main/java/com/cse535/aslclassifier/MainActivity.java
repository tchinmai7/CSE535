package com.cse535.aslclassifier;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_CODE = 1;
    int READ_REQUEST_CODE = 2;
    String filesrc;
    TextView metrics, result;
    VideoView videoView;
     ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        }
        Button fileButton = findViewById(R.id.fileButton);
        fileButton.setOnClickListener(v -> performFileSearch());
        metrics = findViewById(R.id.metricsTv);
        result = findViewById(R.id.resultTV);
        dialog = new ProgressDialog(MainActivity.this);

    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        //intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri content_describer = data.getData();
            if (content_describer != null) {
                filesrc = FileUtils.getRealPath(MainActivity.this, content_describer);
                Log.e("Selected File", filesrc);
                dialog.setTitle("Classifying");
                dialog.setMessage("Loading....");
                String resultVal = classifyFile(filesrc);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    metrics.setText(resultVal);
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!(requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private String classifyFile(String fileName) {
        File csv = new File(fileName);
        try {
            CSVReader reader =  new CSVReaderBuilder(new FileReader(csv.getAbsolutePath())).build();
            long startTime = System.currentTimeMillis();
            int num_about = 0;
            int num_father = 0;
            int num_error = 0;
            int num_rows = 0;
            String about = "About";
            String father = "Father";
            String[] row;
            row = reader.readNext();
            row = reader.readNext();
            while( row != null) {
                num_rows++;
                float[] vals = cleanRow(row);
                int result = classifyRow(vals);
                if (result == 0) {
                    num_about ++;
                } else if (result == 1) {
                    num_father ++;
                } else {
                    num_error ++;
                }
                row = reader.readNext();
            }
            long endTime = System.currentTimeMillis();
            String decision = "Classification Result is : ";
            float accuracy = 0.0f;
            String aboutpath = "android.resource://" + getPackageName() + "/R.raw._about.mp4";
            String fatherPath = "android.resource://" + getPackageName() + "/R.raw._father.mp4";
            if (num_about  > num_father) {
                decision = decision + about;
                accuracy = ((float)num_about / num_rows) * 100;
                videoView.setVideoURI(Uri.parse(aboutpath));
            } else {
                decision = decision + father;
                accuracy = ((float)num_father / num_rows) * 100;
                videoView.setVideoURI(Uri.parse(fatherPath));
            }
            result.setText(decision);
            return "The accuracy of the calculation is : "  + accuracy + " and the prediction using Decision Tree took " + (endTime - startTime) + " ms";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occured during Classification";
        }
    }
    private float[] cleanRow(String[] line) {
        int[] indexes = new int[] {4,5,7,8,10,11,13,14,16,17,19,20,22,23,25,26,28,29,31,32,34,35};

        float[] row = new float[indexes.length + 1];
        int i = 0;
        int j = 1;
        for (i = 0 ; i < line.length; i++) {
            int finalI = i;
            boolean result =IntStream.of(indexes).anyMatch(x -> x == finalI);
            if (result) {
                row[j] = Float.parseFloat(line[i]);
                j++;
            }
        }
        return row;
    }
    private int classifyRow(float[] row) {
        if (row.length != 23) {
            return -1;
        }
        if (row[22] < 161.574) {
            if (row[16] < 173.2) {
                return 0;
            } else {
                if (row[16] < 206.57) {
                    if (row[20] < 210.304) {
                        return 0;
                    } else {
                        if (row[3] < 143.52) {
                            if (row[12] < 151.678) {
                                return 1;
                            } else {
                                return 0;
                            }
                        } else {
                            if (row[2] < 76.2507) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                } else {
                    if (row[4] < 94.2055) {
                        if (row[22] < 129.823) {
                            if (row[5] < 120.139) {
                                if (row[18] < 264.748) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else {
                                if (row[11] < 175.657 ) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        } else {
                            if (row[17] < 28.7567) {
                                if (row[19] < 108.191) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else {
                                if (row[20] < 247.928) {
                                    return 0;
                                } else {
                                    return 1;
                                }

                            }
                        }
                    } else {
                        if (row[13] < 32.873) {
                            return 0;
                        } else {
                            if (row[16] < 207.639) {
                                if (row[18] < 185.649) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else {
                                return 1;
                            }
                        }
                    }
                }
            }
        } else {
            if (row[20] < 284.923) {
                if (row[10] < 176.012) {
                    if (row[9] < 109.793) {
                        if (row[20] < 133.575) {
                            if (row[2] < 115.03) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else {
                            if (row[7] < 121.728) {
                                if (row[1] < 115.098) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if (row[13] < 100.587) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }

                        }
                    } else {
                        if (row[15] < 177.02) {
                            if (row[6] < 97.052) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else {
                            if (row[2] < 71.5512) {
                                return 1;
                            } else {
                                if (row[7] < 137.581) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        }

                    }
                } else {
                    if (row[15] < 182.219) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            } else {
                if (row[13] < 69.5778) {
                    if (row[16] < 248.35) {
                        if (row[1] < 122.106) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        if (row[20] < 336.75) {
                            if (row[17] < 34.7111) {
                                if (row[18] < 208.121) {
                                    return 1;
                                } else {
                                    return 0;
                                }

                            } else {
                                if (row[12] < 195.568) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            if (row[13] < 57.5315) {
                                return 0;
                            } else {
                                if (row[3] < 130.191) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        }
                    }
                } else {
                    if (row[12] < 820.641) {
                        if (row[17] < 20.7043) {
                            if (row[18] < 259.669) {
                                if (row[20] < 334.645) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if (row[20] < 307.44) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        } else {
                            if (row[13] < 76.1159) {
                                if (row[3] < 130.259) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else {
                                return 1;
                            }
                        }
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
